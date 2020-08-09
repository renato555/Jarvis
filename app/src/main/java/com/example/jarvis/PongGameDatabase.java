package com.example.jarvis;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PongGameDatabase {
    private boolean isHost;

    private DatabaseReference player1Ref;
    private DatabaseReference player2Ref;
    private DatabaseReference ballXRef;
    private DatabaseReference ballYRef;
    private DatabaseReference player1ScoreRef;
    private DatabaseReference player2ScoreRef;

    public PongGameDatabase(boolean isHost, String roomName){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //setup reference paths
        player1Ref = database.getReference("rooms/" + roomName + "/player1");
        player2Ref = database.getReference("rooms/" + roomName + "/player2");
        ballXRef = database.getReference("rooms/" + roomName + "/ball/first");
        ballYRef = database.getReference("rooms/" + roomName + "/ball/second");
        player1ScoreRef = database.getReference("rooms/" + roomName + "/player1/score");
        player2ScoreRef  = database.getReference("rooms/" + roomName + "/player2/score");

        player1Ref.setValue( Double.MIN_VALUE);
        player2Ref.setValue(Double.MIN_VALUE);
        ballXRef.setValue(Double.valueOf(0));
        ballYRef.setValue(Double.valueOf(0));
        player2ScoreRef.setValue(Double.valueOf(0));

        this.isHost = isHost;
    }


    public void update(Player player1, Player player2, Ball ball, Score score) {

        if (isHost) {
            //Host writes his position and ball position to database and scores
            player1Ref.setValue((double) player1.getPositionX() / GameView.screenWidth);
            ballXRef.setValue((double) ball.getPositionX() / GameView.screenWidth);
            ballYRef.setValue((double) ball.getPositionY() / GameView.screenHeight);

            //Host pulls opponent position from database when changed
            player2Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() instanceof Double)
                        player2.updatePositionFromDatabase((float) (GameView.screenWidth * (1 - ((double) snapshot.getValue()))));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // do nothing
                }
            });

            ball.update(player1, player2, score);

        } else {
            //Guest writes only his position to database
            player2Ref.setValue((double) player1.getPositionX() / GameView.screenWidth);

            //Guest pulls opponent position
            player1Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() instanceof Double)
                        player2.updatePositionFromDatabase((float) (GameView.screenWidth * (1 - ((double) snapshot.getValue()))));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //do nothing
                }
            });

            //Guest pulls ball position
            ballXRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() instanceof Double && snapshot.getValue() != null)
                        ball.updateBallXPositionFromDatabase((float) (GameView.screenWidth * (1 - ((double) snapshot.getValue()))));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //do nothing
                }
            });
            ballYRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() instanceof Double && snapshot.getValue() != null)
                        ball.updateBallYPositionFromDatabase((float) (GameView.screenHeight * (1 - ((double) snapshot.getValue()))));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //do nothing
                }
            });

            updateScoresForPlayer2(score, ball);
        }
    }

    private void updateScoresForPlayer2(Score score, Ball ball){
        if (ball.getPositionY() >= GameView.screenHeight) {
            //player2 scored
            score.increaseScorePlayer2();
        }else if (ball.getPositionY() <= 0) {
            //player1 scored
            score.increaseScorePlayer1();
        }
    }
}
