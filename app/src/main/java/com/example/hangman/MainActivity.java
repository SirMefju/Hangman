package com.example.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText editInput;
    TextView txtLettersTried;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Letters tried: ";
    TextView txtTriesLeft;
    String triesLeft;
    final String WINNING_MESSAGE = "You won!";
    final String LOSING_MESSAGE = "You lost!";
    Animation rotateAnimation;
    Animation scaleAnimation;
    Animation scaleAndRotationAnimation;
    TableRow trReset;
    TableRow trTriesLeft;
    void revealLetterInWord(char letter){
        int indexOfLetter = wordToBeGuessed.indexOf(letter);
        while(indexOfLetter >= 0){
            wordDisplayedCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }
        //update string
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
    }
    void displayWordOnScreen(){
        String formattedString = "";
        for(char character : wordDisplayedCharArray){
            formattedString += character + " ";
        }
        txtWordToBeGuessed.setText(formattedString);
    }
    void initializeGame(){
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);
        wordDisplayedCharArray = wordToBeGuessed.toCharArray();
        for(int i = 0; i < wordDisplayedCharArray.length - 1; i++){
            wordDisplayedCharArray[i] = '_';
        }
        revealLetterInWord(wordDisplayedCharArray[0]);
        revealLetterInWord(wordDisplayedCharArray[wordDisplayedCharArray.length-1]);
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
        displayWordOnScreen();
        //input letter
        editInput.setText("");
        //letters tried
        lettersTried = " ";
        txtLettersTried.setText(MESSAGE_WITH_LETTERS_TRIED);
        //tries left
        triesLeft = " X X X X X";
        txtTriesLeft.setText(triesLeft);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myListOfWords = new ArrayList<String>();
        txtWordToBeGuessed = (TextView) findViewById(R.id.txtWordToBeGuessed);
        editInput = (EditText) findViewById(R.id.editInput);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        txtTriesLeft = (TextView) findViewById(R.id.txtTriesLeft);
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        scaleAndRotationAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_and_rotate);
        scaleAndRotationAnimation.setFillAfter(true);
        trReset = (TableRow) findViewById(R.id.trReset);
        trTriesLeft = (TableRow) findViewById(R.id.trTriesLeft);
        InputStream myInputStream = null;
        Scanner in = null;
        String aWord = "";
        try {
            myInputStream = getAssets().open("database_file.txt");
            in = new Scanner(myInputStream);
            while(in.hasNext()){
                aWord = in.next();
                myListOfWords.add(aWord);
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getClass().getSimpleName()+": "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        finally {
            if(in != null){
                in.close();
            }
            try {
                if(myInputStream != null) {
                    myInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.getClass().getSimpleName()+": "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        //https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
        initializeGame();
        //setup the text changed listener for the edit text
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    checkIfLetterIsIsWord(s.charAt(0));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    void checkIfLetterIsIsWord(char letter){
        if(wordToBeGuessed.indexOf(letter) >= 0){
            if(wordDisplayedString.indexOf(letter) < 0){
                txtWordToBeGuessed.startAnimation(scaleAnimation);
                revealLetterInWord(letter);
                displayWordOnScreen();
                if(!wordDisplayedString.contains("_")){
                    trTriesLeft.startAnimation(scaleAndRotationAnimation);
                    txtTriesLeft.setText(WINNING_MESSAGE);
                }
            }
        }
        else{
            //number of tries left
            decreaseAndDisplayTriesLeft();
            if(triesLeft.isEmpty()){
                trTriesLeft.startAnimation(scaleAndRotationAnimation);
                txtTriesLeft.setText(LOSING_MESSAGE);
                txtWordToBeGuessed.setText(wordToBeGuessed);
            }
        }
        if(lettersTried.indexOf(letter) < 0){
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            txtLettersTried.setText(messageToBeDisplayed);
        }
    }
    void decreaseAndDisplayTriesLeft(){
        if(!triesLeft.isEmpty()){
            txtTriesLeft.startAnimation(scaleAnimation);
            triesLeft = triesLeft.substring(0,triesLeft.length()-2);
            txtTriesLeft.setText(triesLeft);
        }
    }
    public void resetGame(View v) {
        trReset.startAnimation(rotateAnimation);
        //clear last anim
        trTriesLeft.clearAnimation();
        initializeGame();
    }
}