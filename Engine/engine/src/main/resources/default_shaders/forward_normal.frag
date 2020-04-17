#version 460 core

#define MAX_POINT_LIGHTS 15
#define MAX_SPOTLIGHTS 5
#define MAX_DIRECTIONAL_LIGHTS 1

const float DIFFUSE_STRENGTH = 1f;      //Just 1f I guess
const float OPACITY = 1.0f;

uniform float osc;

struct MaterialProperties
{
    float specularStrength;
    float specularExponent;
    float opacity;
};

/////LIGHT TYPES//////////////////////////////
//Squeezing allowed!
struct PointLight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 color;         //16    16  0   12
    float constant;     //4     28      4
    float linear;       //4     32      4
    float quadratic;    //4     36      4
//std140: Total 40 Pad 8 Size 48
};

struct DirectionalLight
{                   //std140            std430
    vec4 direction; //16    0   1       12
    vec4 color;     //16    16          12
//std140: Total 32 Pad 0 Size 32
};

struct Spotlight
{                       //std140        std430
    vec4 position;      //16    0   1   12
    vec4 direction;     //16    16  1   12
    vec4 color;         //16    32  0   12
    float innerCutOff;  //4     44      4
    float outerCutOff;  //4     48      4
//std140: Total 52 Pad 12 Size 64
};

/////INPUT AND UNIFORMS//////////////
in tangentSpaceLighting
{
    vec3 pointLightPositions[MAX_POINT_LIGHTS];
    vec3 spotlightPositions[MAX_SPOTLIGHTS];
    vec3 spotlightDirections[MAX_SPOTLIGHTS];
    vec3 directionalLightDirections[MAX_DIRECTIONAL_LIGHTS];
};
in vec2 v_TextureCoordinate;
in vec3 v_FragmentPosition;
in vec3 v_ViewPosition;

layout(std140, binding = 1) uniform lightSetup
{
    PointLight pointLights[MAX_POINT_LIGHTS];
    Spotlight spotlights[MAX_SPOTLIGHTS];
    DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
    float numPointLights;
    float numSpotlights;
    float numDirectionalLights;
    float ambientStrength;
};

/////MATERIAL////////////////////////////////////////
layout(binding = 0) uniform sampler2D diffuseTexture;
layout(binding = 1) uniform sampler2D normalMap;
uniform MaterialProperties materialProperties;
/////OUTPUT//////
out vec4 o_Color;

float when_gt(float x, float y)
{
    return max(sign(x - y), 0.0);
}

vec3 calculatePointLight(PointLight light, vec3 position, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = ambientStrength * light.color.rgb * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color.rgb * diff * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;
    //Attenuation
    float distance = length(position - v_FragmentPosition);
    float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return (ambient + diffuse + specular);
}

vec3 calculateSpotlight(Spotlight light, vec3 position, vec3 direction, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(position - v_FragmentPosition);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    float theta = dot(lightDirection, normalize(-direction));
    float epsilon = light.innerCutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0f, 1.0f);
    //Ambient
    vec3 ambient = ambientStrength * light.color.rgb * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color.rgb * diff * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;
    //Cutoff
    //TODO: Maybe add ambient back with attenuation fixed.
    diffuse *= intensity;
    specular *= intensity;
    return (diffuse + specular);
}

vec3 calculateDirectionalLight(DirectionalLight light, vec3 direction, vec3 normal, vec3 viewDirection)
{
    vec3 lightDirection = normalize(-direction);
    vec3 halfWayDirection = normalize(lightDirection + viewDirection);
    //Ambient
    vec3 ambient = ambientStrength * light.color.rgb * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Diffuse
    float diff = max(dot(normal, lightDirection), 0.0f);
    vec3 diffuse = DIFFUSE_STRENGTH * light.color.rgb * diff * texture(diffuseTexture, v_TextureCoordinate).rgb;
    //Specular
    float specularFactor = max(dot(normal, halfWayDirection), 0.0f);
    float spec = pow(specularFactor, materialProperties.specularExponent) * when_gt(diff, 0.0f); //Fixes some leaking
    vec3 specular = materialProperties.specularStrength * light.color.rgb * spec;

    return (ambient + diffuse + specular);
}

void main()
{
    vec3 normal = normalize(texture(normalMap, v_TextureCoordinate).rgb * 2.0f - 1.0f); // Read normals
    vec3 viewDirection = normalize(v_ViewPosition - v_FragmentPosition);
    vec3 result = vec3(0.0f);

    //Calculate light contribution
    for (uint i = 0; i < numPointLights; ++i)
    {
        result += calculatePointLight(pointLights[i], pointLightPositions[i], normal, viewDirection);
    }

    for (uint i = 0; i < numSpotlights; ++i)
    {
        result += calculateSpotlight(spotlights[i], spotlightPositions[i], spotlightDirections[i], normal, viewDirection);
    }

    for (uint i = 0; i < numDirectionalLights; ++i)
    {
        result += calculateDirectionalLight(directionalLights[i], directionalLightDirections[i], normal, viewDirection);
    }

    o_Color = vec4(result, materialProperties.opacity);
}
//strength * lightColor * lightComponent