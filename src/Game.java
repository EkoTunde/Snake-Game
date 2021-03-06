import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends JPanel implements ActionListener {

    // Images that hold snake and food
    private Image food;
    private Image body;
    private Image head_up;
    private Image head_right;
    private Image head_down;
    private Image head_left;
    private Image super_food;

    private final int UP = -1;
    private final int RIGHT = 2;
    private final int DOWN = 1;
    private final int LEFT = -2;
    private int direction;

    private final int BODY_PART_SIZE = 10;
    private int body_length = 3;

    private int[] xs;
    private int[] ys;
    private int food_x;
    private int food_y;
    private int super_food_x;
    private int super_food_y;

    private final Timer timer;

    private boolean playing = true;
    private boolean start = false;
    private int super_on = 0;

    private int score = 0;
    private int super_score = 0;

    private int lap = 0;
    private boolean keyPressed = false;

    public Game() {
        setFocusable(true);
        setPreferredSize(new Dimension(300, 300));
        loadImages();
        addKeyListener(new ControlsAdapter());
        resetData();

        timer = new Timer(10, this);
        timer.start();
    }

    private void loadImages() {
        body = new ImageIcon(getClass().getResource("images/body.png")).getImage();
        food = new ImageIcon(getClass().getResource("images/food.png")).getImage();
        super_food = new ImageIcon(getClass().getResource("images/super_food.png")).getImage();
        head_up = new ImageIcon(getClass().getResource("images/head_up.png")).getImage();
        head_right = new ImageIcon(getClass().getResource("images/head_right.png")).getImage();
        head_down = new ImageIcon(getClass().getResource("images/head_down.png")).getImage();
        head_left = new ImageIcon(getClass().getResource("images/head_left.png")).getImage();
    }

    private void resetData() {

        lap = 0;
        score = 0;
        super_score = 0;
        super_on = 0;
        body_length = 3;

        xs = new int[900];
        ys = new int[900];

        for (int i = 0; i < body_length; i++) {
            xs[i] = 3 - i;
            ys[i] = 5;
        }

        resetFoodPosition();

        direction = RIGHT;
        playing = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        lap = lap > 6 ? 0 : lap + 1;
        if (playing) drawBoard(g);
        else drawGameOver(g);
    }

    private void drawBoard(Graphics g) {
        boolean eaten = food_x == xs[0] && food_y == ys[0];
        if (super_food_x == xs[0] && super_food_y == ys[0] && super_on > 0) {
            super_score++;
            eaten = true;
            super_on = 0;
        }
        if (lap == 7 || keyPressed) {
            keyPressed = false;
            if (eaten) {
                score++;
                resetFoodPosition();
                body_length++;
                xs[body_length] = xs[body_length - 1];
                ys[body_length] = ys[body_length - 1];

                if (score % 10 == 0) {
                    super_on = 300 + (score * 3);
                    resetSuperFoodPosition();
                }
            }
            for (int i = body_length - 1; i > 0; i--) {
                xs[i] = xs[i - 1];
                ys[i] = ys[i - 1];
            }

            if (direction == UP) {
                ys[0] -= 1;
            }
            if (direction == RIGHT) {
                xs[0] += 1;
            }
            if (direction == DOWN) {
                ys[0] += 1;
            }
            if (direction == LEFT) {
                xs[0] -= 1;
            }
            lap = 0;
        }

        if (hasHitWall() || hasHitSelf()) {
            playing = false;
            start = false;
            return;
        }
        g.drawImage(food, food_x * BODY_PART_SIZE, food_y * BODY_PART_SIZE, this);

        drawSnake(g);

        if (super_on > 0) {
            g.drawImage(super_food, super_food_x * BODY_PART_SIZE, super_food_y * BODY_PART_SIZE, this);
        }
        super_on--;
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < body_length; i++) {
            if (i == 0) {
                if (direction == UP) {
                    g.drawImage(head_up, xs[0] * BODY_PART_SIZE, ys[0] * BODY_PART_SIZE, this);
                }
                if (direction == RIGHT) {
                    g.drawImage(head_right, xs[0] * BODY_PART_SIZE, ys[0] * BODY_PART_SIZE, this);
                }
                if (direction == DOWN) {
                    g.drawImage(head_down, xs[0] * BODY_PART_SIZE, ys[0] * BODY_PART_SIZE, this);
                }
                if (direction == LEFT) {
                    g.drawImage(head_left, xs[0] * BODY_PART_SIZE, ys[0] * BODY_PART_SIZE, this);
                }
            } else {
                g.drawImage(body, xs[i] * BODY_PART_SIZE, ys[i] * BODY_PART_SIZE, this);
            }
        }
    }

    private void drawGameOver(Graphics g) {
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fontMetrics = getFontMetrics(small);
        g.setColor(Color.BLUE);
        g.setFont(small);

        String title = "Game Over";
        g.drawString(title, (300 - fontMetrics.stringWidth(title)) / 2, (300 / 2) - 20);

        int total_score = score + (super_score * 5);
        String score = "Score: " + total_score;
        g.drawString(score, (300 - fontMetrics.stringWidth(score)) / 2, (300 / 2));

        String instructions = "Press Enter to continue";
        g.drawString(instructions, (300 - fontMetrics.stringWidth(instructions)) / 2, (300 / 2) + 20);

        Toolkit.getDefaultToolkit().sync();
    }

    private boolean hasHitWall() {
        return xs[0] == -1 || xs[0] == 30 || ys[0] == -1 || ys[0] == 30;
    }

    private boolean hasHitSelf() {
        for (int i = 1; i < body_length; i++) {
            if (xs[0] == xs[i] && ys[0] == ys[i]) {
                return true;
            }
        }
        return false;
    }

    private void resetFoodPosition() {
        int[] newPosition = nextFoodPosition();
        food_x = newPosition[0];
        food_y = newPosition[1];
    }

    private int[] nextFoodPosition() {
        int x = ThreadLocalRandom.current().nextInt(0, 30);
        int y = ThreadLocalRandom.current().nextInt(0, 30);
        for (int i = 0; i < body_length; i++) {
            if (xs[i] == x && ys[i] == y) {
                return nextFoodPosition();
            }
        }
        return new int[]{x, y};
    }

    private void resetSuperFoodPosition() {
        int[] newPosition = newSuperFoodPosition();
        super_food_x = newPosition[0];
        super_food_y = newPosition[1];
    }

    @NotNull
    private int[] newSuperFoodPosition() {
        int x = ThreadLocalRandom.current().nextInt(0, 30);
        int y = ThreadLocalRandom.current().nextInt(0, 30);
        for (int i = 0; i < body_length; i++) {
            if ((xs[i] == x && ys[i] == y) || (food_x == x && food_y == y)) {
                return newSuperFoodPosition();
            }
        }
        return new int[]{x, y};
    }

    private class ControlsAdapter extends KeyAdapter {
        @Override
        public void keyPressed(@NotNull KeyEvent e) {
            keyPressed = true;
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && direction != DOWN) direction = UP;
            if (key == KeyEvent.VK_RIGHT && direction != LEFT) direction = RIGHT;
            if (key == KeyEvent.VK_DOWN && direction != UP) direction = DOWN;
            if (key == KeyEvent.VK_LEFT && direction != RIGHT) direction = LEFT;
            if (key == KeyEvent.VK_ENTER && !playing) resetData();
            if (key == KeyEvent.VK_P && timer.isRunning()) timer.stop();
            if (key == KeyEvent.VK_P && !timer.isRunning()) timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (start && !playing) {
            playing = true;
            resetData();
        }
        repaint();
    }
}