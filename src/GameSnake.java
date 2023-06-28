
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GameSnake {

    final int POINT_RADIUS = 20;
    final int FIELD_WIDTH = 20;
    final int FIELD_HEIGHT = 20;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int DELAY = 150;
    final int LEFT = 37;
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int PAUSE = 32;
    final int MUTE = 77;
    Snake snake;
    Food food;
    GameField gameField;
    StartWindow startWindow;
    Island island;
    JFrame frame, frameSt;
    Clip clip;
    boolean gameOver = false;
    boolean var = false;
    boolean mute = false;
    Random random = new Random();


    ///////////////////////////////////////////MAIN////////////////////////////////////////////////////////////


    public static void main(String[] args) {
        //����� ���� ������
        new GameSnake().st();
        //����� ���� ����
        new GameSnake().go();
    }

    ///////////////////////////////////////////////////GO//////////////////////////////////////////////////////

    //����� ����
    void go() {
        //�������� ���� ����
        frame = new JFrame("Game Snake" + " : " + START_SNAKE_SIZE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + 14, FIELD_HEIGHT * POINT_RADIUS + 37);
        frame.setLocation(300, 300);
        frame.setResizable(false);
        frame.toBack();
        //������� ������ � ���� ����
        gameField = new GameField();
        gameField.setBackground(Color.gray);
        //�������������
        frame.getContentPane().add(BorderLayout.CENTER, gameField);
        //������������ ������
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                try {
                    snake.setDirection(e.getKeyCode());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, PAUSE); //������� ����� ������
        food = new Food(); //������� ����� ����� � ����
        island = new Island(); //������� ����� ������
        island.newIsland();
        //        island.newIsland();
        //        island.newIsland();
        //        island.newIsland();
        //        island.newIsland();
        snake.sound();
        for (int i = 0; i < island.listIsland.size(); i++)
            System.out.println(island.listIsland.get(i).getX() + ", " + island.listIsland.get(i).getY());
        System.out.println("----------------------------------------------------");


        while (!gameOver) { //���� ���� �� ��������
            snake.move(); //������ ����
            if (food.isEaten()) food.next();  //���� ����� ������� - ������� ����� �����

            gameField.repaint(); //����������� � ������� ����� ����� � ������

            try {
                Thread.sleep(DELAY); //�������� �������� ������
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    ///////////////////////////////////////////////////START//////////////////////////////////////////////////////


    //����� ���� �����
    void st() {
        //�������� ����
        frameSt = new JFrame("Game SNAKE");
        frameSt.setVisible(true);
        frameSt.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frameSt.setSize(FIELD_WIDTH * POINT_RADIUS + 13, FIELD_HEIGHT * POINT_RADIUS + 36);
        frameSt.setResizable(false);
        frameSt.setLocation(300, 300);
        frameSt.toFront();
        //������� ������ � ���� ����
        startWindow = new StartWindow();
        startWindow.setBackground(Color.gray);

        frameSt.getContentPane().add(BorderLayout.CENTER, startWindow);
    }


    /////////////////////////////////////////SNAKE/////////////////////////////////////////////////////


    class Snake {
        //������� ������ ������
        ArrayList<Point> snake = new ArrayList<>();
        int direction;

        //��������� ������ �������������� ����.
        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        //����� �������� �� ����� �� ���� ���� �� ����
        boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y) && (direction == PAUSE))
                    return false;
                if ((point.getX() == x) && (point.getY() == y))
                    return true;
            }
            return false;
        }

        //����� ���������, �������� �� ���������� ����� ����
        boolean isFood(Point food) {
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }

        //����� ��������
        void move() {
            //�������� ��������� ������ ����� � ������ ������
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            //�������� ��� ������� �� �������
            if (direction == LEFT) {
                x--;
            }
            if (direction == RIGHT) {
                x++;
            }
            if (direction == UP) {
                y--;
            }
            if (direction == DOWN) {
                y++;
            }
            //������� ������ ���� ��� �� ��������� � ������� ����
            if (x > FIELD_WIDTH - 1) {
                x = 0;
            }
            if (x < 0) {
                x = FIELD_WIDTH - 1;
            }
            if (y > FIELD_WIDTH - 1) {
                y = 0;
            }
            if (y < 0) {
                y = FIELD_HEIGHT - 1;
            }
            //����� ����, �������� ������
            if (isInsideSnake(x, y) || island.isInsideIsland(x, y))
                gameOver = true;

            //��������� � ������ ����� ��������� �����
            snake.add(0, new Point(x, y));

            if (isFood(food)) { //���� ����� ��� ���?
                food.eat(); // ����
                frame.setTitle("Game Snake" + " : " + snake.size()); //������ ����� ������ � ���������
            } else {
                snake.remove(snake.size() - 1); //���� ��������� ����� ������
            }
        }

        //����� ������������ ������
        void sound() {
            try {
                File soundFile = new File("Sound.wav"); //�������� ����
                //�������� AudioInputStream
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                //�������� ���������� ���������� Clip
                clip = AudioSystem.getClip();
                //��������� ��� �������� ����� � Clip
                clip.open(ais);
                clip.setFramePosition(0); //������������� ��������� �� �����
                clip.start(); //�������!!!

            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
                exc.printStackTrace();
            }
        }

        //�������� ������, ���� ������ �� ������������ ���� � ����
        void setDirection(int direction) throws IOException {
            if ((direction >= LEFT) && (direction <= DOWN)) {
                if (Math.abs(this.direction - direction) != 2) {
                    this.direction = direction;
                }
            }
            //�����
            if (direction == PAUSE) this.direction = direction;
            if (this.direction != PAUSE) var = false;
            if (this.direction == PAUSE) var = true;
            //��������� ����
            if (direction == MUTE) {
                if (!mute) {
                    clip.stop(); //������������� ������
                    clip.close(); //��������� �����
                    mute = true;
                } else {
                    mute = false;
                    sound();
                }
            }
        }

        //����� �������� ������
        void paint(Graphics g) {
            for (Point point : snake) {
                point.paint(g);
            }
        }
    }


    ////////////////////////////////////////////////////FOOD/////////////////////////////////////////////


    class Food extends Point {
        //�����������
        public Food() {
            super(-1, -1);
            this.color = Color.RED;
        }

        //����� ������ �����
        void eat() {
            this.setXY(-1, -1);
        }

        //���� ����� ���� �������
        boolean isEaten() {
            return this.getX() == -1;
        }

        Point random() {
            int x, y;
            x = random.nextInt(FIELD_WIDTH);
            y = random.nextInt(FIELD_HEIGHT);
            if ((island.isInsideIsland(x, y)) || (snake.isInsideSnake(x, y)))
                random();
            return new Point(x, y);
        }

        //������ ��������� ��������� ����� ���
        void next() {
            do {
                random();
            } while (snake.isInsideSnake(random().getX(), random().getY()) || island.isInsideIsland(random().getX(), random().getY()));
            this.setXY(random().getX(), random().getY());
            System.out.println(food.random().getX() + ", " + food.random().getY());
        }
    }


    ////////////////////////////////////////////ISLAND/////////////////////////////////////////////////////


    class Island {
        List<Point> listIsland = new ArrayList<>();

        void list(int x, int y) {
            listIsland.add(new Point(x, y));
            listIsland.add(new Point(x, y + 1));
            listIsland.add(new Point(x + 1, y));
            listIsland.add(new Point(x + 1, y + 1));
            listIsland.add(new Point(x, y - 1));
            listIsland.add(new Point(x - 1, y));
            listIsland.add(new Point(x - 1, y - 1));
            listIsland.add(new Point(x + 1, y - 1));
            listIsland.add(new Point(x - 1, y + 1));
        }

        //������ ��������� �������
        void newIsland() {
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x, y) || isInsideIsland(x, y));
            list(x, y);
        }

        //������ ������
        void paint(Graphics g) {
            g.setColor(Color.BLACK);
            for (Point point : listIsland) {
                g.fillRect(point.getX() * POINT_RADIUS, point.getY() * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
            }
        }

        //���� ������ ������ �� ������, ����� ����
        boolean isInsideIsland(int x, int y) {
            for (Point point : listIsland) {
                if ((point.getX() == x) && (point.getY() == y))
                    return true;
            }
            return false;
        }
    }


    ////////////////////////////////////////////POINT/////////////////////////////////////////////////////


    class Point {
        int x, y;
        Color color = Color.green;

        //�����������
        public Point(int x, int y) {
            this.setXY(x, y);
        }

        //����� �������� ����� ���
        void paint(Graphics g) {
            g.setColor(color);
            g.fillRect(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }

        //������ ����� �
        int getX() {
            return x;
        }

        //������ ����� �
        int getY() {
            return y;
        }

        //������ ������������ �����
        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    /////////////////////////////////////////////���� ����////////////////////////////////////////////////


    public class GameField extends JPanel {

        @Override
        public void paint(Graphics q) {
            super.paint(q);
            snake.paint(q);
            island.paint(q);
            food.paint(q);
            //���������� �����
            q.setColor(Color.DARK_GRAY);
            for (int i = 0; i < 400; i += 20) {
                for (int j = 0; j < 400; j += 20) {
                    q.drawRect(i, j, POINT_RADIUS, POINT_RADIUS);
                    q.drawRect(j, i, POINT_RADIUS, POINT_RADIUS);
                }
            }
            //���� ���� ���������, ������� ���� ��������
            if (gameOver) {
                q.setColor(Color.red);
                q.setFont(new Font("Arial", Font.BOLD, 38));
                FontMetrics fm = q.getFontMetrics();
                q.drawString("GAME OVER", (FIELD_WIDTH * POINT_RADIUS - fm.stringWidth("GAME OVER")) / 2,
                        (FIELD_HEIGHT * POINT_RADIUS) / 2);
            }
            //������� �����
            if (var) {
                q.setColor(Color.red);
                q.setFont(new Font("Arial", Font.BOLD, 48));
                FontMetrics p = q.getFontMetrics();
                q.drawString("PAUSE", (FIELD_WIDTH * POINT_RADIUS - p.stringWidth("PAUSE")) / 2,
                        (FIELD_HEIGHT * POINT_RADIUS) / 2);
            }
        }
    }


    /////////////////////////////////////////////���� �����////////////////////////////////////////////////


    public class StartWindow extends JPanel {

        @Override
        public void paint(Graphics q) {
            //������ ���� �����
            JButton button = new JButton("Start game!");
            frameSt.add(button);
            button.setBounds(150, 220, 100, 30);
            button.setVisible(true);
            //������� �������� ����
            super.paint(q);
            q.setColor(Color.green);
            q.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics start = q.getFontMetrics();
            q.drawString("SNAKE", (FIELD_WIDTH * POINT_RADIUS - start.stringWidth("SNAKE")) / 2,
                    (FIELD_HEIGHT * POINT_RADIUS) / 2);
            q.setColor(Color.red);
            q.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics rules = q.getFontMetrics();
            q.drawString("SPASE - Pause", (FIELD_WIDTH * POINT_RADIUS - rules.stringWidth("SPASE - Pause")) / 2,
                    310);
            q.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics rules2 = q.getFontMetrics();
            q.drawString("M - Mute sound", (FIELD_WIDTH * POINT_RADIUS - rules2.stringWidth("M - Mute sound")) / 2,
                    350);
            //��������� ������������ ������
            button.addActionListener(e -> {
                frameSt.toBack();
                frameSt.dispose();
            });
        }
    }
}

