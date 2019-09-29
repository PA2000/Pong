import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.w3c.dom.css.Rect;

public class Pong extends Application{
    Stage stage;
    Scene intro, scene, finaleYou, finaleComp;

    public static final int WIN_WIDTH = 800;
    public static final int WIN_HEIGHT = 600;

    public static final int BALL_RADIUS = 10;
    public static final int PADDLE_WIDTH =10, OPP_WIDTH = 10;
    public static final int PADDLE_HEIGHT = 100, OPP_HEIGHT = 100;

    public int speedX = 4, speedY = 4;
    public int dx =2, dy =2;

    public int p1Pts = 0, p2Pts = 0;

    AnimationTimer timer, timer2;

    Circle ball = new Circle(BALL_RADIUS, Color.BLUE);
    Rectangle paddle = new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT, Color.BLUE);
    Rectangle opponent = new Rectangle(OPP_WIDTH, OPP_HEIGHT, Color.BLUE);
    Text p1Text, p2Text;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("PONG");

        intro = new Scene(createContentIntro(), WIN_WIDTH, WIN_HEIGHT);
        scene = new Scene(createContent(), WIN_WIDTH, WIN_HEIGHT);
        scene.setFill(Color.BLACK);
        scene.setOnMouseMoved(event -> {
            if(event.getY() > 0 && event.getY() < WIN_HEIGHT - PADDLE_HEIGHT) {
                paddle.setLayoutY(event.getY());
            }
        });

        finaleYou = new Scene(createContentEndYou(), WIN_WIDTH, WIN_HEIGHT);
        finaleYou.setFill(Color.BLACK);


        finaleComp = new Scene(createContentEndComp(), WIN_WIDTH, WIN_HEIGHT);
        finaleComp.setFill(Color.BLACK);


        stage.setScene(intro);
        intro.setOnMouseClicked(event -> {
            stage.setScene(scene);
        });
        stage.setResizable(false);
        stage.show();
    }

    public void restart(){
        p1Pts = 0;
        p1Text.setText("You: " + p1Pts);
        p2Pts = 0;
        p2Text.setText("Computer: " + p2Pts);
        dx = 2;
        dy = 2;
        speedX = 3;
        speedY = 3;
        stage.setScene(scene);
    }

    public Parent createContent(){
        Pane root = new Pane();

        Line line = new Line(WIN_WIDTH/2, 0, WIN_WIDTH/2, WIN_HEIGHT);
        line.setStroke(Color.WHITE);

        p1Text = new Text(150,50 ,"YOU: " + p1Pts);
        p1Text.setFont(new Font(20));
        p1Text.setFill(Color.WHITE);

        p2Text = new Text(550,50 ,"COMPUTER: " + p2Pts);
        p2Text.setFont(new Font(20));
        p2Text.setFill(Color.WHITE);

        paddle.setLayoutX(0);
        paddle.setLayoutY(WIN_HEIGHT/2 -PADDLE_HEIGHT/2);

        opponent.setLayoutX(WIN_WIDTH - OPP_WIDTH);
        opponent.setLayoutY(WIN_HEIGHT/2 -OPP_HEIGHT/2);

        ball.setLayoutX(WIN_WIDTH/2);
        ball.setLayoutY(WIN_HEIGHT/2);

        root.getChildren().addAll(line, paddle, opponent, ball, p1Text, p2Text);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameUpdate();
            }
        };
        timer.start();

        return root;
    }

    public Parent createContentIntro() {
        Pane menu = new Pane();
        Image img = new Image("/res/menu.png");
        ImageView menuImg = new ImageView(img);
        menu.getChildren().addAll(menuImg);

        return menu;
    }

    public Parent createContentEndYou() {
        Pane end = new Pane();
        Image img = new Image("/res/you.png");
        ImageView you = new ImageView(img);
        end.getChildren().addAll(you);

        return end;
    }

    public Parent createContentEndComp() {
        Pane end = new Pane();
        Image img = new Image("/res/comp.png");
        ImageView comp = new ImageView(img);
        end.getChildren().addAll(comp);

        return end;
    }

    public void gameUpdate() {
        double x = ball.getLayoutX(), y = ball.getLayoutY();

        //if ball is touching player/opponent paddle
        if(x <= 10 && y > paddle.getLayoutY() && y < paddle.getLayoutY() + PADDLE_HEIGHT) {
            dx = speedX;
        }
        if(x >= WIN_WIDTH -12.5 && y > opponent.getLayoutY() && y < opponent.getLayoutY() + OPP_HEIGHT) {
            speedX++;
            dx = -speedX;
        }

        //HANDLES: WALL BOUNCES
        if(x <= 0) {
            dx = speedX;
            p2Pts++;
            p2Text.setText("COMPUTER: " + p2Pts);
        }
        if(x >= WIN_WIDTH -5) {
            dx = -speedX;
            p1Pts++;
            p1Text.setText("YOU: " + p1Pts);
        }
        if(y <= 0) {
            dy = speedY;
        }
        if(y >= WIN_HEIGHT -5) {
            dy = -speedY;
        }

        /*Moves ball at specified factor:
            WHEN dx is positive:
                ball is moving to right(gaining ground)
            WHEN dx is negative:
                ball is moving to left(losing ground)
            WHEN dy is positive:
                ball is moving to down(gaining ground)
            WHEN dy is positive:
                ball is moving to up(losing ground)
         */
        ball.setLayoutX(ball.getLayoutX() + dx);
        ball.setLayoutY(ball.getLayoutY() + dy);

        //Moves Opponent Paddle["AI"]
        if(x > WIN_WIDTH/2 && opponent.getLayoutY() > y) {
            opponent.setLayoutY(opponent.getLayoutY() - 7);
        }

        if(x > WIN_WIDTH/2 && opponent.getLayoutY() + PADDLE_HEIGHT < y) {
            opponent.setLayoutY(opponent.getLayoutY() + 7);
        }

        if(p1Pts == 11 && p1Pts > p2Pts){
            stage.setScene(finaleYou);
            finaleYou.setOnKeyPressed(event -> {
                if(event.getCode() == KeyCode.ESCAPE){
                    System.exit(0);
                }
            });
            finaleYou.setOnMouseClicked(event -> {
                restart();

            });
        }
        if(p2Pts == 11 && p2Pts > p1Pts){
            stage.setScene(finaleComp);
            finaleComp.setOnKeyPressed(event -> {
                if(event.getCode() == KeyCode.ESCAPE){
                    System.exit(0);
                }
            });
            finaleComp.setOnMouseClicked(event -> {
                restart();
            });
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
