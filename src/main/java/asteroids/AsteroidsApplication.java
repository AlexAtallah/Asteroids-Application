package asteroids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public class AsteroidsApplication extends Application {

    public static int WIDTH = 700;
    public static int HEIGHT = 600;

    public static void main(String[] args) throws Exception {
        launch(args);

    }


    public void start(Stage app) {
        Pane pane = new Pane();
        Text text = new Text(10, 20, "Points: 0");
        pane.getChildren().add(text);
        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        List<Projectile> projectiles = new ArrayList<>();
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        pane.getChildren().add(ship.getCharacter());

        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        AtomicInteger points = new AtomicInteger();

        asteroids.forEach(asteroid -> asteroid.turnRight());
        asteroids.forEach(asteroid -> asteroid.turnRight());
        asteroids.forEach(asteroid -> asteroid.turnRight());
        asteroids.forEach(asteroid -> asteroid.accelerate());
        asteroids.forEach(asteroid -> asteroid.accelerate());
        asteroids.forEach(asteroid -> asteroid.accelerate());

        Scene scene = new Scene(pane);
        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });
        

        new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (Math.random() < 0.005) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

                if (pressedKeys.getOrDefault(KeyCode.A, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.D, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.W, false)) {
                    ship.accelerate();
                }
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < 3) {

                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);

                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    pane.getChildren().add(projectile.getCharacter());
                }

                ship.move();
                asteroids.forEach(asteroid -> asteroid.move());
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });
                projectiles.forEach(projectile -> projectile.move());
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }
                    });

                    if (!projectile.isAlive()) {
                        text.setText("Points: " + points.addAndGet(1000));
                    }
                });

                projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .forEach(projectile -> pane.getChildren().remove(projectile.getCharacter()));
                projectiles.removeAll(projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));

                asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                asteroids.removeAll(asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));

            }
        }.start();

        app.setTitle("Asteroids!");
        app.setScene(scene);
        app.show();
    }

}
