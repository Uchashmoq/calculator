package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculator.math.Evaluator;
import com.example.calculator.math.ExpressionException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int SCALE = 16;
    private Vibrator vibrator;
    private TextView inputView;
    private TextView outputView;
    private TextView modView;
    private TextView focusedTextView;

    private BigInteger mod;

    private BigDecimal mr;

    private Handler handler;

    private ExecutorService executorService;

    private Thread mainThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler=new Handler(Looper.getMainLooper());
        executorService= Executors.newSingleThreadExecutor();
        mainThread=Looper.getMainLooper().getThread();
        initView();
        initVibrator();
        initSymbolButtons();
    }

    private void initSymbolButtons(){
        initSymbolButton(R.id.cal_bt_0,"0");
        initSymbolButton(R.id.cal_bt_1,"1");
        initSymbolButton(R.id.cal_bt_2,"2");
        initSymbolButton(R.id.cal_bt_3,"3");
        initSymbolButton(R.id.cal_bt_4,"4");
        initSymbolButton(R.id.cal_bt_5,"5");
        initSymbolButton(R.id.cal_bt_6,"6");
        initSymbolButton(R.id.cal_bt_7,"7");
        initSymbolButton(R.id.cal_bt_8,"8");
        initSymbolButton(R.id.cal_bt_9,"9");
        initSymbolButton(R.id.cal_bt_dot,".");

        initSymbolButton(R.id.cal_bt_plus,"+");
        initSymbolButton(R.id.cal_bt_minus,"-");
        initSymbolButton(R.id.cal_bt_mul,"*");
        initDiv();
        initSymbolButton(R.id.cal_bt_pow,"^");

        initSymbolButton(R.id.cal_bt_lp,"(");
        initSymbolButton(R.id.cal_bt_rp,")");

        initMod();
        initC();

        initMr();
        initMc();

        initEq();
        initSqrt();

        iniFloorFunction(R.id.cal_bt_2);
        iniCeilFunction(R.id.cal_bt_8);
        iniRandPrimeFunction(R.id.cal_bt_6);
        iniFactorizeFunction(R.id.cal_bt_4);
        iniBaseConvertFunction(R.id.cal_bt_5);
    }

    private void iniBaseConvertFunction(int cal_bt_5) {
        View bt5 = findViewById(cal_bt_5);
        bt5.setOnLongClickListener(v -> {
            String expr =  (String)outputView.getText();
            vibrate(50);
            if(!expr.isEmpty()){
               executorService.submit(()->{
                   try {
                       BigDecimal decimal = new BigDecimal(expr);
                       String result =  String.format("[BIN(%s),OCT(%s),HEX(%s)]",
                               Evaluator.convertToBase(decimal,2,5),
                               Evaluator.convertToBase(decimal,8,5),
                               Evaluator.convertToBase(decimal,16,5)
                       );
                       setText(outputView,result);
                   }catch (RuntimeException e){
                       toast("Base conversion failure");
                   }
               });
            }
            return true;
        });
    }

    private void iniFactorizeFunction(int cal_bt_4) {
        View bt2 = findViewById(cal_bt_4);
        bt2.setOnLongClickListener(v -> {
            String expr =  (String)outputView.getText();
            vibrate(50);
            executorService.submit(()->{
                if(!expr.isEmpty()){
                    try {
                        BigInteger integer = new BigInteger(expr);
                        if(mod!=null) integer=integer.mod(mod);
                        setText(outputView,Evaluator.factorize(integer).toString());
                    }catch (RuntimeException e){
                        toast("Failure to decompose prime factors");
                    }
                }
            });
            return true;
        });
    }

    private void iniRandPrimeFunction(int cal_bt_6) {
        findViewById(cal_bt_6).setOnLongClickListener(v->{
            String expr =  (String)outputView.getText();
            vibrate(50);
            if(!expr.isEmpty()){
                try {
                    BigDecimal decimal = new BigDecimal(expr);
                    BigInteger floor = decimal.setScale(0, RoundingMode.CEILING).toBigInteger();
                    executorService.submit(()->{
                        setText(outputView,floor.nextProbablePrime().toString());
                    });
                }catch (RuntimeException e){
                    toast("Failure to generate a prime number");
                }
            }
            return true;
        });
    }

    private void iniCeilFunction(int cal_bt_8) {
        View bt2 = findViewById(cal_bt_8);
        bt2.setOnLongClickListener(v -> {
            String expr =  (String)outputView.getText();
            vibrate(50);
            if(!expr.isEmpty()){
                try {
                    BigDecimal decimal = new BigDecimal(expr);
                    if(!Evaluator.isInteger(decimal)){
                        setText(outputView,decimal.setScale(0, RoundingMode.CEILING).toBigInteger().toString());
                    }
                }catch (RuntimeException e){
                    toast("Failure to ceil");
                }
            }
            return true;
        });
    }

    private void iniFloorFunction(int cal_bt_2) {
        View bt2 = findViewById(cal_bt_2);
        bt2.setOnLongClickListener(v -> {
            String expr =  (String)outputView.getText();
            vibrate(50);
            if(!expr.isEmpty()){
                try {
                    BigDecimal decimal = new BigDecimal(expr);
                    if(!Evaluator.isInteger(decimal)){
                         setText(outputView,decimal.setScale(0, RoundingMode.FLOOR).toBigInteger().toString());
                    }
                }catch (RuntimeException e){
                    toast("Failure to round down");
                }
            }
            return true;
        });
    }

    private void initSqrt() {
        TextView btSqrt = (TextView) findViewById(R.id.cal_bt_sqrt);
        btSqrt.setOnClickListener(v -> {
            evalInputSqrt();
            vibrate(50);
        });
    }

    private void initEq() {
        TextView btEq = (TextView) findViewById(R.id.cal_bt_eq);
        btEq.setOnClickListener(v -> {
            evalInput();
            vibrate(50);
        });
    }

    private void evalInputSqrt(){
        String expr = (String) inputView.getText();
        if (expr.isEmpty()) return;
        executorService.submit(()->{
            try {
                if(mod==null){
                    double result = Evaluator.eval(expr).doubleValue();
                    setText(outputView,Evaluator.integerApproximation(Math.sqrt(result))+"");
                }else{
                    BigInteger result = Evaluator.eval(expr,mod);
                    List<BigInteger> sqrts = Evaluator.findQuadraticResidue(result,mod);
                    setText(outputView,numsStr(sqrts));
                }
            }catch (RuntimeException e){
                toast("failed to evaluate: "+e.getMessage());
            }
        });
    }


    private static String numsStr(List<? extends Number> nums){
        StringJoiner sj =new StringJoiner(", ","[","]");
        for (Number num : nums){
            sj.add(num.toString());
        }
        return sj.toString();
    }

    private void evalInput(){
        String expr = (String) inputView.getText();
        if (expr.isEmpty()) return;
        executorService.submit(()->{
            BigInteger M = mod;
            try {
                if(M==null){
                    BigDecimal result = Evaluator.eval(expr);
                    setText(outputView,bigDecimalStr(result));
                }else{
                    BigInteger result = Evaluator.eval(expr,M);
                    setText(outputView,result.toString());
                }
            }catch (RuntimeException e){
                toast("failed to evaluate: "+e.getMessage());
            }
        });
    }

    private void initMc() {
        TextView btMc = (TextView) findViewById(R.id.cal_bt_mc);
        btMc.setOnClickListener(v -> {
            mr=null;
            vibrate(50);
        });
    }

    private void initMr() {
        TextView btMr = (TextView) findViewById(R.id.cal_bt_mr);
        btMr.setOnClickListener(v -> {
            if (mr!=null){
                appendText(focusedTextView,mr.toString());
                vibrate(50);
            }
        });

        btMr.setOnLongClickListener(v -> {
            String num=(String) outputView.getText();
            if(num.isEmpty()){
                mr=null;
            }else{
                try {
                    mr= new BigDecimal(num);
                }catch (NumberFormatException e){}
            }
            vibrate(50);
            return true;
        });
    }

    private void initDiv() {
        initSymbolButton(R.id.cal_bt_div,"÷");
        TextView btDiv = (TextView) findViewById(R.id.cal_bt_div);
        btDiv.setOnLongClickListener(view ->{
            appendText(focusedTextView,"/");
            vibrate(50);
            return true;
        });
    }

    private void initC() {
        TextView btC = (TextView) findViewById(R.id.cal_bt_c);
        btC.setOnClickListener(view ->{
            popChar(focusedTextView);
            vibrate(50);
        });

        btC.setOnLongClickListener(view->{
            setText(focusedTextView,"");
            vibrate(50);
            return true;
        });

    }

    private void initMod(){
        TextView btMod = (TextView) findViewById(R.id.cal_bt_mod);
        btMod.setOnClickListener(view -> {
            appendText(focusedTextView,"%");
            vibrate(50);
        });

        btMod.setOnLongClickListener(view -> {
            vibrate(50);
            String expr = (String) modView.getText();
            if (focusedTextView==modView){
                if (expr.isEmpty()){
                    mod=null;
                    Log.d("mod","null");
                    focusedTextView=inputView;
                }else{
                    executorService.submit(()->{
                        try {
                            BigDecimal res = Evaluator.eval(expr);
                            mod=Evaluator.toBigInteger(res);
                            Log.d("mod",mod.toString());
                            setText(modView,"mod("+mod+")");
                            focusedTextView=inputView;
                        }catch (ExpressionException e){
                            toast("failed to set modulus: "+e.getMessage());
                        }
                    });
                }
            }else{
                if(!expr.isEmpty() ){
                    setText(modView,expr.substring(4,expr.length()-1));
                }
                focusedTextView=modView;
                toast("Enter modulus");
            }
            return true;
        });
    }

    private void toast(String msg){
        if(mainThread==Thread.currentThread()){
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
        }else{
            handler.post(()->{
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void setText(TextView tv,CharSequence cs){
        if(mainThread==Thread.currentThread()){
            Log.d("setText",cs.toString());
            audit((String) cs);
            tv.setText(cs);
            setTextSize(tv);
        }else{
            handler.post(()->{
                Log.d("setText",cs.toString());
                audit((String) cs);
                tv.setText(cs);
                setTextSize(tv);
            });
        }
    }

    private TextView initSymbolButton(int id,String symbol){
        TextView tv = (TextView) findViewById(id);
        tv.setOnClickListener(view -> {
            appendText(focusedTextView,symbol);
            vibrate(50);
        });
        return tv;
    }

    private void setTextSize(TextView tv){
        if (tv==inputView){
            tv.setTextSize(30);
        }else{
            tv.setTextSize(20);
        }
    }
    private void appendText(TextView tv,String text){
        Log.d("appendText",text);
        String newText = tv.getText() + text;
        setText(tv,newText);
    }

    private void popChar(TextView tv){
        String text = (String) tv.getText();
        if (text.isEmpty()) return ;
        setText(tv,text.substring(0,text.length()-1));
        return ;
    }

    private void initView(){
        inputView=findViewById(R.id.tv_input);
        outputView=findViewById(R.id.tv_output);
        modView=findViewById(R.id.tv_mod);
        focusedTextView=inputView;
    }

    private void initVibrator(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void vibrate(int time){
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(time);
            }
        }
    }
    private static String bigDecimalStr(BigDecimal n){
        String str = n.toPlainString();
        for (int i=0;i<str.length();i++){
            if(str.charAt(i)=='.'){
                return str.substring(0,Math.min(str.length(),i+20));
            }
        }
        return str;
    }

    private void audit(String s){
        if("8964".equals(s) || "110101195306153019".equals(s)){
            ImageView vWarn = (ImageView) findViewById(R.id.img_warn);
            vWarn.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vWarn.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }
}