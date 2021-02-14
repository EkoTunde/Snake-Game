import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends JPanel implements ActionListener {

    private Image food;
    private Image body;
    private Image head;
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

    private Timer timer;

    private boolean playing = true;

    private boolean inGame = false;

    private boolean start = false;

    private int score = 0;

    private int super_score = 0;

    private int time = 15;

    public Game() {
        setFocusable(true);
        setPreferredSize(new Dimension(300, 300));
        init();
    }

    public void init() {

        inGame = true;

        reset();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_UP:
                        if (direction != DOWN) {
                            direction = UP;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != LEFT) {
                            direction = RIGHT;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != UP) {
                            direction = DOWN;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction != RIGHT) {
                            direction = LEFT;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (!playing) {
                            reset();
                            playing = true;
                        }
                        break;
                    case KeyEvent.VK_P:
                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();
                        }
                        break;
                    default:
                        direction = RIGHT;
                        break;
                }
            }
        });

        ImageIcon iib = new ImageIcon("src/resources/body.png");
        body = iib.getImage();
        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
        ImageIcon iif = new ImageIcon("src/resources/food.png");
        food = iif.getImage();
        ImageIcon iis = new ImageIcon("src/resources/super_food.png");
        super_food = iis.getImage();

        ImageIcon iiu = new ImageIcon("src/resources/head_up.png");
        head_up = iiu.getImage();
        ImageIcon iir = new ImageIcon("src/resources/head_right.png");
        head_right = iir.getImage();
        ImageIcon iid = new ImageIcon("src/resources/head_down.png");
        head_down = iid.getImage();
        ImageIcon iil = new ImageIcon("src/resources/head_left.png");
        head_left = iil.getImage();

        timer = new Timer(10, this);
        timer.start();
    }

    private void reset() {

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

        int[] new_position = nextFoodPosition();
        food_x = new_position[0];
        food_y = new_position[1];

        int[] new_super_position = nextSuperFoodPosition();
        super_food_x = new_super_position[0];
        super_food_y = new_super_position[1];

        direction = RIGHT;
        playing = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (time == 15) {
            time = 1;
        } else {
            time++;
        }
        if (start && !playing) {
            playing = true;
            reset();
        }
        if (playing) {
            drawBoard(g);
        } else {
            drawEnd(g);
        }
    }

    private int super_on = 0;

    private void drawBoard(Graphics g) {
        if (super_food_x == xs[0] && super_food_y == ys[0] && super_on > 0) {
            super_score += 1;
            super_on = 0;
        }

        boolean eaten = food_x == xs[0] && food_y == ys[0];
        if (time == 7 || time == 15) {
            if (eaten) {
                score += 1;
                int[] new_positions = nextFoodPosition();
                food_x = new_positions[0];
                food_y = new_positions[1];
                body_length++;
                xs[body_length] = xs[body_length - 1];
                ys[body_length] = ys[body_length - 1];

                if (score % 10 == 0) {
                    super_on = 300;
                    int[] new_super_positions = nextFoodPosition();
                    super_food_x = new_super_positions[0];
                    super_food_y = new_super_positions[1];
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
        }

        if (hasHitWall() || hasHitSelf()) {
            playing = false;
            start = false;
            return;
        }
        g.drawImage(food, food_x * BODY_PART_SIZE, food_y * BODY_PART_SIZE, this);

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

        if (super_on > 0) {
            g.drawImage(super_food, super_food_x * BODY_PART_SIZE, super_food_y * BODY_PART_SIZE, this);
        }
        super_on--;
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawEnd(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fontMetrics = getFontMetrics(small);
        g.setColor(Color.BLUE);
        g.setFont(small);
        g.drawString(msg, (300 - fontMetrics.stringWidth(msg)) / 2, (300 / 2) - 20);

        int total_score = score + (super_score * 5);
        String msg_2 = "Score: " + total_score;
        g.setColor(Color.BLUE);
        g.setFont(small);
        g.drawString(msg_2, (300 - fontMetrics.stringWidth(msg_2)) / 2, (300 / 2));

        String msg_3 = "Press Enter to continue";
        g.setColor(Color.BLUE);
        g.setFont(small);
        g.drawString(msg_3, (300 - fontMetrics.stringWidth(msg_3)) / 2, (300 / 2) + 20);

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

    private int[] nextSuperFoodPosition() {
        int x = ThreadLocalRandom.current().nextInt(0, 30);
        int y = ThreadLocalRandom.current().nextInt(0, 30);
        for (int i = 0; i < body_length; i++) {
            if ((xs[i] == x && ys[i] == y) || (food_x == x && food_y == y)) {
                return nextSuperFoodPosition();
            }
        }
        return new int[]{x, y};
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}