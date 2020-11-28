import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.sun.glass.ui.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.shape.*;

import java.util.Map;
import java.util.Random;
import java.lang.*;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.almasb.fxgl.ui.FXGLUIConfig.getUIFactory;

public class MainGame extends GameApplication {
    //declaration
    private Entity background, player1, player2;
    private boolean isp1Forward = true, isp2Forward = false;
    private boolean isp1punching = false, isp1kicking = false;
    private boolean isp2punching = false, isp2kicking = false;

    Random rand = new Random();
    //for initializing the game window
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Deadly Kombat");
        settings.setVersion("1.5");
        settings.setWidth(1200);
        settings.setHeight(700);
    }

    //for initializing the game entities
    @Override
    protected void initGame() {
        background = FXGL.entityBuilder()
                .at(0,0)
                .view("background.png")
                .buildAndAttach();

        player1 = FXGL.entityBuilder()
                .at(0,580-110)
                .view("p1stance.gif")
                .with(new CollidableComponent(true))
                .buildAndAttach();

        player2 = FXGL.entityBuilder()
                .at(1000, 580-120)
                .view("p2stance.gif")
                .with(new CollidableComponent(true))
                .buildAndAttach();


    }


    //for initializing the user input
    @Override
    protected void initInput() {
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Player1 Go right") {

            @Override
            protected void onAction() {
                isp1Forward=true;
                player1.setView(FXGL.getAssetLoader().loadTexture("p1run.gif")); //changing the player image
                if(player1.getX() < player2.getX() - 10){ //right bound for player
                    player1.translateX(2);
                }
            }

            @Override
            protected void onActionEnd() {
                player1.setView(FXGL.getAssetLoader().loadTexture("p1stance.gif")); //changing the player image
            }
        }, KeyCode.RIGHT);


        input.addAction(new UserAction("Player1 Go left") {

            @Override
            protected void onAction() {
                isp1Forward=false;
                player1.setView(FXGL.getAssetLoader().loadTexture("p1run_flipped.gif")); //changing the player image
                if(player1.getX() > 0){ //right bound for player
                    player1.translateX(-2);
                }
            }

            @Override
            protected void onActionEnd() {
                player1.setView(FXGL.getAssetLoader().loadTexture("p1stance_flipped.gif")); //changing the player image
            }
        }, KeyCode.LEFT);


        input.addAction(new UserAction("Player1 Jump") {
            @Override
            protected void onActionBegin() {
                if (player1.getX() < 1040 && isp1Forward) {
                    for (int i = 0; i <= 200; i += 50) {
                        getMasterTimer().runOnceAfter(() -> {
                            player1.translate(10, -30); //player is moving up
                        }, Duration.millis(i));
                    }
                    for (int i = 200; i <= 400; i += 50) {
                        getMasterTimer().runOnceAfter(() -> {
                            player1.translate(10, 30); //player is moving down
                        }, Duration.millis(i));
                    }
                } else if (player1.getX() > 0 && !isp1Forward) {
                    for (int i = 0; i <= 200; i += 50) {
                        getMasterTimer().runOnceAfter(() -> {
                            player1.translate(-10, -30); //player is moving up
                        }, Duration.millis(i));
                    }
                    for (int i = 200; i <= 400; i += 50) {
                        getMasterTimer().runOnceAfter(() -> {
                            player1.translate(-10, 30); //player is moving down
                        }, Duration.millis(i));
                    }
                }
            }
        }, KeyCode.SPACE);


        input.addAction(new UserAction("Player1 Punch") {
            @Override
            protected void onAction() {
                if (isp1Forward) {
                    player1.setView(FXGL.getAssetLoader().loadTexture("p1combo.gif")); //changing the player image
                    if (Math.abs(player1.getX() - player2.getX()) < 110) {
                        isp1punching = true;
                        if (!isp2Forward) {
                            int n = rand.nextInt(10);
                            if (n % 4 == 0) {
                                player2.setView(FXGL.getAssetLoader().loadTexture("p2kick.gif"));
                                //isp2kicking = true;
                            }
                            else if (n % 4 == 1) {
                                player2.setView(FXGL.getAssetLoader().loadTexture("p2combo.gif"));
                                //isp2punching = true;
                            }
                        }
                        if (isp1punching == true) {
                            FXGL.getGameState().increment("Player2Health", -10);
                        }
                    }
                }
                else {
                    player1.setView(FXGL.getAssetLoader().loadTexture("p1combo_flipped.gif"));
                    if (Math.abs(player1.getX() - player2.getX()) < 110) {
                        if (isp2Forward) {
                            int n = rand.nextInt(10);
                            if (n % 4 == 0) player2.setView(FXGL.getAssetLoader().loadTexture("p2kick_flipped.gif"));
                            else if (n % 4 == 1) player2.setView(FXGL.getAssetLoader().loadTexture("p2combo_flipped.gif"));
                        }
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                if (isp1Forward) player1.setView(FXGL.getAssetLoader().loadTexture("p1stance.gif")); //changing the player image
                else player1.setView(FXGL.getAssetLoader().loadTexture("p1stance_flipped.gif"));
                if (isp2Forward) player2.setView(FXGL.getAssetLoader().loadTexture("p2stance.gif"));
                else player2.setView(FXGL.getAssetLoader().loadTexture("p2stance_flipped.gif"));
            }
        }, KeyCode.X);


        input.addAction(new UserAction("Player1 Kick") {

            @Override
            protected void onAction() {
                if (isp1Forward) {
                    player1.setView(FXGL.getAssetLoader().loadTexture("p1kick.gif")); //changing the player image
                    if (Math.abs(player1.getX() - player2.getX()) < 110) {
                        if (!isp2Forward) {
                            int n = rand.nextInt(10);
                            if (n % 4 == 0) player2.setView(FXGL.getAssetLoader().loadTexture("p2kick.gif"));
                            else if (n % 4 == 1) player2.setView(FXGL.getAssetLoader().loadTexture("p2combo.gif"));
                        }
                    }
                }
                else {
                    player1.setView(FXGL.getAssetLoader().loadTexture("p1kick_flipped.gif")); //changing the player image
                    if (Math.abs(player1.getX() - player2.getX()) < 110) {
                        if (isp2Forward) {
                            int n = rand.nextInt(10);
                            if (n % 4 == 0) player2.setView(FXGL.getAssetLoader().loadTexture("p2kick_flipped.gif"));
                            else if (n % 4 == 1)
                                player2.setView(FXGL.getAssetLoader().loadTexture("p2combo_flipped.gif"));
                        }
                    }
                }
            }

            @Override
            protected void onActionEnd() {
                if (isp1Forward) player1.setView(FXGL.getAssetLoader().loadTexture("p1stance.gif")); //changing the player image
                else player1.setView(FXGL.getAssetLoader().loadTexture("p1stance_flipped.gif"));
                if (isp2Forward) player2.setView(FXGL.getAssetLoader().loadTexture("p2stance.gif"));
                else player2.setView(FXGL.getAssetLoader().loadTexture("p2stance_flipped.gif"));
            }

        }, KeyCode.Z);

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("Player1Health", 300);
        vars.put("Player2Health", 300);
    }

    //for displaying additional elements like health etc.
    @Override
    protected void initUI() {
        Text healthPlayer1 = new Text();
        Text healthPlayer2 = new Text();

        Rectangle healthbarPlayer1 = new Rectangle();
        healthbarPlayer1.setX(50);
        healthbarPlayer1.setY(100);
        healthbarPlayer1.setWidth(300);
        healthbarPlayer1.setHeight(20);
        healthbarPlayer1.setFill(Color.RED);

        healthbarPlayer1.widthProperty().bind(FXGL.getGameState().intProperty("Player1Health"));

        healthPlayer1.setX(50);
        healthPlayer1.setY(90);
        healthPlayer1.setText("Player 1");
        healthPlayer1.setFont(new Font("Consolas", 25));
        healthPlayer1.setFill(Color.RED);

        Rectangle healthbarPlayer2 = new Rectangle();
        healthbarPlayer2.setX(800);
        healthbarPlayer2.setY(100);
        healthbarPlayer2.setWidth(300);
        healthbarPlayer2.setHeight(20);
        healthbarPlayer2.setFill(Color.BLUE);

        healthbarPlayer2.widthProperty().bind(FXGL.getGameState().intProperty("Player2Health"));

        healthPlayer2.setX(1000);
        healthPlayer2.setY(90);
        healthPlayer2.setText("Player 2");
        healthPlayer2.setFont(new Font("Consolas", 25));
        healthPlayer2.setFill(Color.BLUE);

        FXGL.getGameScene().addUINode(healthbarPlayer1); // add to the scene graph
        FXGL.getGameScene().addUINode(healthbarPlayer2); // add to the scene graph
        FXGL.getGameScene().addUINode(healthPlayer1);
        FXGL.getGameScene().addUINode(healthPlayer2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
