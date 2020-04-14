package engine.graphics;

@SuppressWarnings({"unused", "WeakerAccess"})
public class NormalMaterial extends Material
{
    private Texture diffuseTexture, normalMap;

    public NormalMaterial(String diffuseTexture, String normalMap)
    {
        this(ResourceManager.getTexture(diffuseTexture), ResourceManager.getTexture(normalMap));
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap)
    {
        super(Shader.NORMAL);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    public NormalMaterial(String diffuseTexture, String normalMap, float specularStrength, float specularExponent, float opacity)
    {
        this(ResourceManager.getTexture(diffuseTexture), ResourceManager.getTexture(normalMap), specularStrength, specularExponent, opacity);
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap, float specularStrength, float specularExponent, float opacity)
    {
        super(Shader.NORMAL, 1f, specularStrength, specularExponent, opacity);
        this.diffuseTexture = diffuseTexture;
        this.normalMap = normalMap;
    }

    public NormalMaterial(String diffuseTexture, String normalMap, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        this(ResourceManager.getTexture(diffuseTexture), ResourceManager.getTexture(normalMap), diffuseStrength, specularStrength, specularExponent, opacity);
    }

    public NormalMaterial(Texture diffuseTexture, Texture normalMap, float diffuseStrength, float specularStrength, float specularExponent, float opacity)
    {
        super(Shader.NORMAL, diffuseStrength, specularStrength, specularExponent, opacity);
    }

    @Override
    public void bind()
    {
        super.bind();
        diffuseTexture.bind(0);
        normalMap.bind(1);
    }

    @Override
    public void unbind()
    {
        shader.unbind();
        diffuseTexture.unbind(0);
        normalMap.unbind(1);
    }

    public void delete()
    {
        unbind();
        diffuseTexture.delete();
        normalMap.delete();
    }
}
