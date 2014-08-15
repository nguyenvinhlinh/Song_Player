/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cosc2101;

import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author nguyenvinhlinh
 */
public class SongPlayer extends Application {

    private String network = ""; //online, offline
    private String sourceType = "fullurl";// id, fullurl
    private String source = ""; //the id of youtube video, or only the full url of online song 
    @Override
    public void init() throws Exception {
        System.out.println("init()");
        super.init();
        Parameters params = getParameters();
        Map<String, String> namedParameters = params.getNamed();
        for (Map.Entry<String, String> entry : namedParameters.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " - Value: " + entry.getValue());
            if (entry.getKey().equals("network")) {
                network = entry.getValue();
            } else if (entry.getKey().equals("type")) {
                sourceType = entry.getValue();
            } else if (entry.getKey().equals("source")) {
                source = entry.getValue();
            }
        }
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Song Player");
        if (network.equals("online")) {
            playOnlineSong(stage);
        } else if (network.equals("offline")) {
            playOfflineSong(stage);
        } else {
            System.out.println("Parameter error: invalid parameter - " + network);
        }
    }
    public void playOnlineSong(Stage stage) throws Exception {
        WebView webview = new WebView();
        if (sourceType.equals("fullurl")) {
            webview.getEngine().load(source);
        } else if (sourceType.equals("id")) {
            String fullURL = "http://www.youtube.com/embed/" + source + "?autoplay=1";
            webview.getEngine().load(fullURL);
        } else {
            System.out.println("Parameter error: invalid parameter - " + sourceType);
            return;
        }
        webview.setPrefSize(640, 390);
        stage.setScene(new Scene(webview));
        stage.show();
    }
    public void playOfflineSong(final Stage stage) {
        Group root = new Group();
        Media media;
        if (sourceType.equals("fullurl")) {
            media = new Media(source);
        } else if (sourceType.equals("id")) {
            media = new Media("prefix" + source + "postfix");
        } else {
            System.out.println("Parameter error: invalid parameter - " + sourceType);
            return;
        }
        //check the exist of file
        final MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);
        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();
        final Slider volumeSlider = new Slider();
        root.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                slideOut.play();
            }
        });
        root.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                slideIn.play();
            }
        });
        final VBox vbox = new VBox();
        final Slider slider = new Slider();
        vbox.getChildren().add(slider);
        //add a button for controlling play pause status, and a bar to control the sound level

        final HBox hbox2 = new HBox(3);
        hbox2.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox2, Pos.CENTER);
        final Button playButton = new Button("Play");
        hbox2.getChildren().add(playButton);

        final Button stopButton = new Button("Stop");
        hbox2.getChildren().add(stopButton);

        Label volumeLabel = new Label("Volume");
        hbox2.getChildren().add(volumeLabel);
        //sound level
        volumeSlider.setPrefWidth(200);
        volumeSlider.setMaxWidth(250);
        volumeSlider.setMinWidth(100);
        volumeSlider.setValue(100);
        hbox2.getChildren().add(volumeSlider);

        vbox.getChildren().add(hbox2);
        root.getChildren().add(view);
        root.getChildren().add(vbox);

        Scene scene = new Scene(root, 400, 400, Color.BLACK);
        stage.setScene(scene);
        stage.show();

        player.play();
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                int w = player.getMedia().getWidth();
                int h = player.getMedia().getHeight();
                /*
                 hbox.setMinWidth(w);
                 int bandWidth = w/rects.length;
                 for (Rectangle r:rects) {
                 r.setWidth(bandWidth);
                 r.setHeight(2);
                 }
                 */
                stage.setMinWidth(w);
                stage.setMinHeight(h);
                vbox.setMinSize(w, 100);
                vbox.setTranslateY(h - 100);
                slider.setMin(0.0);
                slider.setValue(0.0);
                slider.setMax(player.getTotalDuration().toSeconds());

                slideOut.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(), h - 100),
                                new KeyValue(vbox.opacityProperty(), 0.9)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(), h),
                                new KeyValue(vbox.opacityProperty(), 0.0)
                        )
                );
                slideIn.getKeyFrames().addAll(
                        new KeyFrame(new Duration(0),
                                new KeyValue(vbox.translateYProperty(), h),
                                new KeyValue(vbox.opacityProperty(), 0.0)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vbox.translateYProperty(), h - 100),
                                new KeyValue(vbox.opacityProperty(), 0.9)
                        )
                );
            }
        });
        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration current) {
                slider.setValue(current.toSeconds());
            }
        });
        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.seek(Duration.seconds(slider.getValue()));
            }
        });
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Status status = player.getStatus();
                if (status == Status.PLAYING) {
                    player.pause();
                } else {
                    player.play();
                }
            }

        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                player.stop();
            }
        });
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                if (volumeSlider.isValueChanging() == true) {
                    player.setVolume(volumeSlider.getValue() / 100);
                }
            }

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
