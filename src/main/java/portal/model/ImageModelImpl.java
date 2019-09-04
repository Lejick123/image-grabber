package portal.model;

import java.awt.image.BufferedImage;

public class ImageModelImpl {
    BufferedImage image;
    String name;
    String md5;

    public ImageModelImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
