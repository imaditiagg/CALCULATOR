package com.example.aditi.my_calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private static final String OPERATORS =  "*/+-";
    private static final HashMap<String,Integer> prec = new HashMap<>();

    String currentToken = null;
    String tokensString = "";
    String operator=null;
    boolean isLastTokenOperation = false;
    boolean isLastOperationEvaluated = false;

    ArrayList<String>  infixTokens= new ArrayList<>();
    TextView stackTextView;
    TextView resultTextView;
    TextView operatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prec.put("*",2);
        prec.put("/",2);
        prec.put("+",1);
        prec.put("-",1);
        stackTextView = findViewById(R.id.stack);
        resultTextView = findViewById(R.id.result);
        operatorView=findViewById(R.id.operator);
    }

    public void onClick(View view){
        String text = ((Button)view).getText().toString();
        String tag = (String) view.getTag();
        if(isLastOperationEvaluated){ //if last operation is evaluated
            if(infixTokens.size()-1>=0)
                infixTokens.remove(infixTokens.size()-1);
        }
        if(tag.equals("point")){ //if current token is NULL then make it 0
            if(operator!=null)
            {
                tokensString =tokensString.concat(operator);
                stackTextView.setText(tokensString);
                operator=null;
            }
            if(currentToken == null){
                currentToken = "0";
            }
            if(!currentToken.contains(".")){ //if it doesn't contain a point already
                currentToken = currentToken.concat(text);
                tokensString = tokensString.concat(text);
                isLastTokenOperation = false;
                isLastOperationEvaluated = false;
                stackTextView.setText(tokensString);
            }

        }
        if(tag.equals("value")){ //if it is a value

            if(operator!=null)
            {
                tokensString =tokensString.concat(operator);
                stackTextView.setText(tokensString);
                operator=null;
            }

            if(currentToken == null){
                currentToken = "";
            }
            operatorView.setText("");

            currentToken = currentToken.concat(text);
            tokensString = tokensString.concat(text);
            isLastOperationEvaluated = false;
            isLastTokenOperation = false;
            stackTextView.setText(tokensString);

            eval();
        }
        if(tag.equals("equal")){
            show();

        }
        if(tag.equals("operation")){


            //handle "-" as symbol
            if(isLastTokenOperation && text.equals("-") || currentToken==null && text.equals("-"))
            {
                if(currentToken == null){ //when - enters in the beginning
                    currentToken = "";
                }
                currentToken =currentToken.concat(text); //update currentToken
                isLastTokenOperation=false;
                isLastOperationEvaluated = false;
                if(operator!=null) {
                    tokensString = tokensString.concat(operator);
                    operator=null;
                    operatorView.setText("");
                }
                tokensString=tokensString.concat(text);
                stackTextView.setText(tokensString); //update stackTextview
            }

            else if(currentToken != null && !isLastTokenOperation && !currentToken.equals("-") ){ //when last token was not operator
                operator=text;
                infixTokens.add(currentToken); //add current token in list
                currentToken = "";
                isLastOperationEvaluated = false;
                isLastTokenOperation = true;
                infixTokens.add(text); //add operator in list
                operatorView.setText(text);

            }
            else if(currentToken!=null && isLastTokenOperation){ //when last token was operator
                operator=text;
                currentToken="";
                isLastTokenOperation=true;
                isLastOperationEvaluated = false;
                infixTokens.remove(infixTokens.size()-1);
                infixTokens.add(text); //change the operator
                operatorView.setText(text);


            }
        }
        if(tag.equals("clear")){ //delete everything
            infixTokens.clear();
            tokensString = "";
            currentToken = null;
            operator=null;
            operatorView.setText("");
            isLastTokenOperation = false;
            isLastOperationEvaluated = false;
            stackTextView.setText("");
            resultTextView.setText("");
            resultTextView.setTextSize(30);
        }
    }

    public void eval(){
        if(infixTokens.size() >= 2 && !isLastTokenOperation){
            infixTokens.add(currentToken);
            Double result = evaluate(); //evaluate the result
            isLastOperationEvaluated = true;
            isLastTokenOperation = false;
            resultTextView.setText(result+"");
        }
    }
    public Double evaluate(){
        ArrayList<String> postfixTokens = infixToPostfix(infixTokens);
        Stack<String> stack = new Stack<>();
        for(String token: postfixTokens){
            if(OPERATORS.contains(token)){ //if it is an operator
                Double val2 = Double.parseDouble(stack.pop());
                Double val1 = Double.parseDouble(stack.pop());
                Double result = operate(token,val1,val2);
                stack.push(result+"");
            }
            else {
                stack.push(token);
            }
        }
        return Double.parseDouble(stack.pop());
    }

    private Double operate(String token, Double val1, Double val2) {
        switch (token){
            case "*": return val1 * val2;
            case "/": return val1/val2;
            case "+": return val1+ val2;
            case "-": return val1 -val2;
            default:return  Double.MIN_VALUE;
        }
    }

    public ArrayList<String> infixToPostfix(ArrayList<String> infix){
        ArrayList<String>postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for(String token: infix){
            if(OPERATORS.contains(token)){
                while (!stack.isEmpty()){
                    String topOperator = stack.peek();
                    if(prec.get(topOperator) >= prec.get(token)){
                        postfix.add(stack.pop());
                    }
                    else {
                        break;
                    }
                }
                stack.push(token);
            }
            else {
                postfix.add(token);
            }

        }
        while (!stack.isEmpty()){
            postfix.add(stack.pop());
        }
        return postfix;
    }

    public void show()
    {
        resultTextView.setTextSize(40); //increase the size of result
    }
}
