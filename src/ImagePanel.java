import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private PixelClickListener listener;

    public ImagePanel(BufferedImage image) {
        this.image = image;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                double scaleX = (double) imageWidth / panelWidth;
                double scaleY = (double) imageHeight / panelHeight;

                int x = (int) (e.getX() * scaleX);
                int y = (int) (e.getY() * scaleY);

                if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
                    int rgb = image.getRGB(x, y);

                    if (listener != null) {
                        listener.onPixelClicked(x, y, rgb);
                    }
                }
            }
        });
    }

    public void setPixelClickListener(PixelClickListener listener) {
        this.listener = listener;
    }

    public void setImage(BufferedImage newImage) {
        this.image = newImage;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }
}