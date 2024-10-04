import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main implements PixelClickListener {
    private BufferedImage originalImage;
    private BufferedImage changedImage;
    private Pixel pixelBase;
    Stack<Pixel> stackPixel;
    Queue<Pixel> queuePixel;
    private int count = 0;
    private ImagePanel imagePanel;
    private Color selectedColor = Color.WHITE;
    private boolean useQueue = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        try {
            File inputFile = new File("testepequeno.png");
            BufferedImage inputImage = ImageIO.read(inputFile);
            originalImage = inputImage;
            changedImage = inputImage;

            JFrame frame = new JFrame("Editor de Imagens");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setSize(inputImage.getWidth(), inputImage.getHeight() + 100);

            imagePanel = new ImagePanel(inputImage);
            imagePanel.setPixelClickListener(this);

            frame.add(imagePanel, BorderLayout.CENTER);

            JButton colorButton = new JButton("Escolher cor");
            colorButton.addActionListener(e -> {
                Color color = JColorChooser.showDialog(null, "Escolha uma cor", selectedColor);
                if (color != null) {
                    selectedColor = color;
                    System.out.println("Cor escolhida: " + selectedColor);
                }
            });

            JToggleButton toggleButton = new JToggleButton("Usar Fila");
            toggleButton.addActionListener(e -> {
                if (toggleButton.isSelected()) {
                    toggleButton.setText("Usar Pilha");
                    useQueue = false;
                } else {
                    toggleButton.setText("Usar Fila");
                    useQueue = true;
                }
            });

            JPanel controlPanel = new JPanel();
            controlPanel.add(colorButton);
            controlPanel.add(toggleButton);

            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setVisible(true);

        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem: " + e.getMessage());
        }
    }

    @Override
    public void onPixelClicked(int x, int y, int rgb) {
        repaintImage(x, y);
    }

    public void repaintImage(int x, int y) {
        int color = originalImage.getRGB(x, y);

        pixelBase = new Pixel(x, y, color);

        stackPixel = new Stack<>(originalImage.getHeight() * originalImage.getWidth());
        queuePixel = new Queue<>(originalImage.getHeight() * originalImage.getWidth());

        stackPixel.push(pixelBase);
        queuePixel.add(pixelBase);

        changeNextPixels(x, y);

        startPaiting(useQueue);
    }

    private void startPaiting(boolean queue) {
        SwingWorker<Void, Pixel> worker = new SwingWorker<Void, Pixel>() {
            @Override
            protected Void doInBackground() {
                if (queue) {
                    while (!queuePixel.isEmpty()) {
                        Pixel pixelPintar = queuePixel.remove();
                        publish(pixelPintar);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    while (!stackPixel.isEmpty()) {
                        try {
                            Pixel pixelPintar = stackPixel.pop();
                            publish(pixelPintar);
                            try {
                                Thread.sleep(2);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Pixel> chunks) {
                for (Pixel pixelPintar : chunks) {
                    changedImage.setRGB(pixelPintar.getX(), pixelPintar.getY(), selectedColor.getRGB());
                }
                imagePanel.setImage(changedImage);
                imagePanel.repaint();
            }

            @Override
            protected void done() {
                try {

                    File outputfile = new File("imagem_modificada.png");
                    ImageIO.write(changedImage, "png", outputfile);
                    System.out.println("Imagem salva com sucesso: " + outputfile.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Erro ao salvar a imagem: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void changeNextPixels(int x, int y) {
        if (getNextPixels(x - 1, y)) {
            changeNextPixels(x - 1, y);
        }

        if (getNextPixels(x + 1, y)) {
            changeNextPixels(x + 1, y);
        }

        if (getNextPixels(x, y + 1)) {
            changeNextPixels(x, y + 1);
        }

        if (getNextPixels(x, y - 1)) {
            changeNextPixels(x, y - 1);
        }
    }

    private boolean getNextPixels(int x, int y) {
        if ((x >= 0) && (x < originalImage.getWidth()) && (y >= 0) && (y < originalImage.getHeight())) {
            int colorPixel = originalImage.getRGB(x, y);
            Pixel newPixel = new Pixel(x, y, colorPixel);
            if (colorPixel == pixelBase.getColorBity() && (!queuePixel.contains(newPixel))) {
                stackPixel.push(newPixel);
                queuePixel.add(newPixel);
                count += 1;
                return true;
            }
        }
        return false;
    }
}