package uci.atcnea.student.utils;

/**
 * Created by koyi on 30/05/16.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

import uci.atcnea.student.R;
import uci.atcnea.student.activity.QuizActivity;
import uci.atcnea.student.model.Connection;
import uci.atcnea.student.model.DividerDraw;
import uci.atcnea.student.model.TextColumn;
import uci.atcnea.student.model.TextDraw;


public class ConnectPatternView extends View {

    private final long ANIMATION_DURATION = 0;//300;
    private final int ANIMATION_TYPE_NONE = 0;
    private final int ANIMATION_TYPE_MIDDLE = 1;
    private final int ANIMATION_TYPE_BOTTOM = 2;
    private int numbersOfConnectors = 6;
    private int circleColor = Color.BLACK;
    private int lineColor = Color.LTGRAY;
    private int lineWidth = 7; //in dp
    private int radius = 14; //in dp
    private int diameter = 28; //in dp
    private int dp48 = 48; //in dp
    private int animationType = ANIMATION_TYPE_MIDDLE;
    private Drawable drawable;

    private int leftX;
    private int topY;
    private int rightX;
    private int bottomY;
    private int centerX;
    private int centerY;
    private int topX;
    private int maxY;

    private Paint pCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pDivider = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Rect touchPoint = new Rect();

    private TextColumn textColumn = new TextColumn();

    /**
     * Flag to know if firts touch was in A column
     */
    private Boolean touchA = false;

    /**
     * Array of all connected circles to draw
     */
    private ArrayList<Connection> connected;

    private ArrayList<Integer> userResponse;

    /**
     * Array of all possible circles to draw
     */
    private Rect[] circles = new Rect[9];
    private ArrayList<Rect> circlesA = new ArrayList<>();
    private ArrayList<Boolean> markedA = new ArrayList<>();
    private ArrayList<Rect> circlesB = new ArrayList<>();
    private ArrayList<Boolean> markedB = new ArrayList<>();

    private ArrayList<String> listItem1;
    private ArrayList<String> listItem2;

    /**
     * Array of indexes of circles based on the number of circles that were chosen
     */
    private int[] indexes;
    private int[] indexesA;
    private int[] indexesB;

    /**
     * Array of indexes of circles in order they were connected
     */
    private ArrayList<Integer> connectionOrder = new ArrayList<>();

    private OnConnectPatternListener mPatternListener;
    private OnViewDrawedListener mDrawedListener;
    private OnTouchDrawListener mTouchDrawListener;

    /**
     * Constructor for ConnectPatternView
     *
     * @param context Context
     */
    public ConnectPatternView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructor for ConnectPatternView
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public ConnectPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Constructor for ConnectPatternView
     *
     * @param context      Context
     * @param attrs        AttributeSet
     * @param defStyleAttr Style
     */
    public ConnectPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ConnectPatternView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initialize the ConnectPatternView widget
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    private void init(Context context, AttributeSet attrs) {

        connected = new ArrayList<>();

        float multi = context.getResources().getDisplayMetrics().density;
        lineWidth *= multi;
        radius *= multi;
        diameter *= multi;
        dp48 *= multi;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConnectPatternView);
        try {
//            numbersOfConnectors = typedArray.getInt(R.styleable.ConnectPatternView_connectPatternNumber, numbersOfConnectors);
            circleColor = typedArray.getColor(R.styleable.ConnectPatternView_connectPatternCircleColor, circleColor);
            radius = (int) typedArray.getDimension(R.styleable.ConnectPatternView_connectPatternCircleRadius, radius);
            diameter = radius * 2;
            lineColor = typedArray.getColor(R.styleable.ConnectPatternView_connectPatternLineColor, lineColor);
            lineWidth = (int) typedArray.getDimension(R.styleable.ConnectPatternView_connectPatternLineWidth, lineWidth);
            drawable = typedArray.getDrawable(R.styleable.ConnectPatternView_connectPatternDrawable);
            animationType = typedArray.getInt(R.styleable.ConnectPatternView_connectPatternAnimationType, animationType);
        } finally {
            typedArray.recycle();
        }

        switch (numbersOfConnectors) {
            case 2:
                indexes = new int[]{0, 2};
                break;
            case 3:
                indexes = new int[]{0, 2, 4};
                break;
            case 5:
                indexes = new int[]{0, 2, 4, 6, 8};
                break;
            case 9:
                indexes = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
                break;
            default:
                break;
        }

        pCircle.setColor(circleColor);
        pLine.setColor(lineColor);
        pLine.setStrokeWidth(lineWidth);

        pText.setTypeface(Typeface.DEFAULT);
        pText.setTextSize(17);
        pText.setColor(Color.parseColor("#202020"));

        pDivider.setColor(Color.parseColor("#e0e0e0"));
        pDivider.setStrokeWidth(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        leftX = getPaddingLeft();
        leftX = (int) (w * 0.35);

        topY = getPaddingTop();
        topX = w;

//        rightX = w - getPaddingRight();
        rightX = w - leftX;

        bottomY = h - getPaddingBottom();

        centerX = leftX + (rightX - leftX) / 2;
        centerY = topY + (bottomY - topY) / 2;

        setupCircles();

    }

    /**
     * Setup circles on a screen
     */
    private void setupCircles() {
        circles[0] = new Rect(leftX, topY, leftX + diameter, topY + diameter);
        circles[1] = new Rect(centerX - radius, topY, centerX + radius, topY + diameter);
        circles[2] = new Rect(rightX - diameter, topY, rightX, topY + diameter);
        circles[3] = new Rect(leftX, centerY - radius, leftX + diameter, centerY + radius);
        circles[4] = new Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        circles[5] = new Rect(rightX - diameter, centerY - radius, rightX, centerY + radius);
        circles[6] = new Rect(leftX, bottomY - diameter, leftX + diameter, bottomY);
        circles[7] = new Rect(centerX - radius, bottomY - diameter, centerX + radius, bottomY);
        circles[8] = new Rect(rightX - diameter, bottomY - diameter, rightX, bottomY);


        if (circlesA.size() == 0) {

            fillLeftCirclesAndText();
            fillRightCirclesAndText();
            if (mDrawedListener != null) {
                mDrawedListener.onDrawedFinish();
            }
        }
    }

    public void fillLeftCirclesAndText() {

        int starX = 10;
        int posY = 30;
        int endX = leftX - 10;
        int lineSpace = 3;
        int itemSpace = 30;
        int width = endX - starX;
        int itemStartY = 0;
        Rect bound = new Rect();
        pText.getTextBounds("BB", 0, "BB".length(), bound);
        int lineHeight = bound.height();

        for (int i = 0; i < listItem1.size(); i++) {
            String[] text = listItem1.get(i).split(" ");

            String oldText = "";
            String newText = "";
            int w = 0;
            int k = 0;

            while (k < text.length) {
                newText = oldText + " " + text[k];
                Rect bounds = new Rect();
                pText.getTextBounds(newText, 0, newText.length(), bounds);
                w = bounds.width();
                if (w > width) {
                    TextDraw textDraw = new TextDraw(oldText, starX, posY);
                    textColumn.addTextDraw(textDraw);
//                    canvas.drawText(oldText, starX, posY, pText);
                    posY += lineHeight + lineSpace;
                    oldText = "";
                } else {
                    oldText += " " + text[k];
                    k++;
                }

            }
            if (oldText.length() > 0) {
                TextDraw textDraw = new TextDraw(oldText, starX, posY);
                textColumn.addTextDraw(textDraw);
//                canvas.drawText(oldText, starX, posY, pText);
                posY += lineHeight + lineSpace;
            }
            DividerDraw dividerDraw = new DividerDraw(starX, endX + 10 + diameter, posY);
            textColumn.addDivider(dividerDraw);
//            canvas.drawLine(starX, posY, endX, posY, pDivider);

            int circleY = itemStartY + ((posY - itemStartY) / 2) - radius;
            circlesA.add(new Rect(leftX, circleY, leftX + diameter, circleY + diameter));
            markedA.add(false);
            itemStartY = posY;
            posY += itemSpace;
        }


//        int k = 10;
//        for (int i = 0; i < listItem1.size(); i++){
//            circlesA.add(new Rect(leftX, k, leftX + diameter, k + diameter));
//            markedA.add(false);
//            circlesB.add(new Rect(rightX - diameter, k, rightX, k + diameter));
//            markedB.add(false);
//            k += 80;
//        }

    }

    public void fillRightCirclesAndText() {

        int starX = rightX + 10;
        int posY = 30;
        int endX = topX - 10;
        int lineSpace = 3;
        int itemSpace = 30;
        int width = endX - starX;
        int itemStartY = 0;
        Rect bound = new Rect();
        pText.getTextBounds("BB", 0, "BB".length(), bound);
        int lineHeight = bound.height();

        for (int i = 0; i < listItem2.size(); i++) {
            String[] text = listItem2.get(i).split(" ");

            String oldText = "";
            String newText = "";
            int w = 0;
            int k = 0;

            while (k < text.length) {
                newText = oldText + " " + text[k];
                Rect bounds = new Rect();
                pText.getTextBounds(newText, 0, newText.length(), bounds);
                w = bounds.width();
                if (w > width) {
                    TextDraw textDraw = new TextDraw(oldText, starX, posY);
                    textColumn.addTextDraw(textDraw);
//                    canvas.drawText(oldText, starX, posY, pText);
                    posY += lineHeight + lineSpace;
                    oldText = "";
                } else {
                    oldText += " " + text[k];
                    k++;
                }

            }
            if (oldText.length() > 0) {
                TextDraw textDraw = new TextDraw(oldText, starX, posY);
                textColumn.addTextDraw(textDraw);
//                canvas.drawText(oldText, starX, posY, pText);
                posY += lineHeight + lineSpace;
            }
            DividerDraw dividerDraw = new DividerDraw(starX - 10 - diameter, endX, posY);
            textColumn.addDivider(dividerDraw);
//            canvas.drawLine(starX, posY, endX, posY, pDivider);

            int circleY = itemStartY + ((posY - itemStartY) / 2) - radius;
            circlesB.add(new Rect(rightX - diameter, circleY, rightX, circleY + diameter));
            markedB.add(false);
            itemStartY = posY;
            posY += itemSpace;
            if (posY > maxY)
                maxY = posY;
        }
    }

    public void setListItems(ArrayList<String> l1, ArrayList<String> l2) {
        this.listItem1 = l1;
        this.listItem2 = l2;
        userResponse = new ArrayList<>();
        for (int i = 0; i < listItem1.size(); i++) {
            userResponse.add(-1);
        }
    }

    public void updateResponse() {
//        userResponse = new ArrayList<>();
        for (int i = 0; i < listItem1.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < connected.size(); j++) {
                if (connected.get(j).getIndexA() == i) {
                    flag = true;
                    userResponse.set(i, connected.get(j).getIndexB());
                    break;
                }
            }
            if (!flag) {
                userResponse.set(i, -1);
            }
        }
        if (mDrawedListener != null)
            mDrawedListener.onConnectionChanged();

    }

    public ArrayList<Integer> getUserResponse() {
//        updateResponse();
        return userResponse;
    }

    public int getMaxHeight() {
        return maxY + 10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);
        drawLineToTouchPoint(canvas);
        drawConections(canvas);
        drawCircles(canvas);
        drawLeftText(canvas);

//        for (int i = 0; i < point.size(); i++){
//            String[] v = point.get(i).split("#");
//            canvas.drawPoint(Float.parseFloat(v[0]), Float.parseFloat(v[1]), pPoint);
//        }
//
//        for (int i = 0; i < circlesA.size(); i++){
//            canvas.drawRect(circlesA.get(i), pCircle);
//        }
//        for (int i = 0; i < circlesB.size(); i++){
//            canvas.drawRect(circlesB.get(i), pCircle);
//        }
    }

    /**
     * Draw text of left column
     */
    private void drawLeftText(Canvas canvas) {
        ArrayList<TextDraw> texts = textColumn.getTexts();
        for (int i = 0; i < texts.size(); i++) {
            TextDraw text = texts.get(i);
            canvas.drawText(text.getText(), text.getX(), text.getY(), pText);
        }

        ArrayList<DividerDraw> dividers = textColumn.getDividerDraws();
        for (int i = 0; i < dividers.size(); i++) {
            DividerDraw divider = dividers.get(i);
            canvas.drawLine(divider.getStartX(), divider.getY(), divider.getEndX(), divider.getY(), pDivider);
        }

    }

    /**
     * Setup circles on a screen
     */
    private void drawLineToTouchPoint(Canvas canvas) {
        if (connectionOrder.size() == 1) {
            if (touchPoint.height() > 0 && touchPoint.width() > 0) {
                if (touchA)
                    drawLine(canvas, circlesA.get(connectionOrder.get(0)), touchPoint);
                else
                    drawLine(canvas, circlesB.get(connectionOrder.get(0)), touchPoint);
            }
        }
    }

    /**
     * Draw lines between the actual connectors
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {

        if (connectionOrder.size() == 2) {
            if (touchA)
                drawLine(canvas, circlesA.get(connectionOrder.get(0)), circlesB.get(connectionOrder.get(1)));
            else
                drawLine(canvas, circlesB.get(connectionOrder.get(0)), circlesA.get(connectionOrder.get(1)));
        }
//        for (int i = 0; i < connectionOrder.size() - 1; i++) {
//            drawLine(canvas, circles[connectionOrder.get(i)], circles[connectionOrder.get(i + 1)]);
//        }
    }

    /**
     * Draw lines between the connectors
     *
     * @param canvas
     */
    private void drawConections(Canvas canvas) {
        for (int i = 0; i < connected.size(); i++) {
            drawLine(canvas, circlesA.get(connected.get(i).getIndexA()), circlesB.get(connected.get(i).getIndexB()));
        }
    }

    /**
     * Draw the line between the touch point and previous point
     *
     * @param canvas
     */
    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < circlesA.size(); i++) {
//            int circleNumber = indexes[i];
            if (drawable == null) {
                canvas.drawCircle(circlesA.get(i).centerX(), circlesA.get(i).centerY(), radius, pCircle);
            } else {
                drawable.setBounds(circlesA.get(i));
                drawable.draw(canvas);
            }
        }
        for (int i = 0; i < circlesB.size(); i++) {
//            int circleNumber = indexes[i];
            if (drawable == null) {
                canvas.drawCircle(circlesB.get(i).centerX(), circlesB.get(i).centerY(), radius, pCircle);
            } else {
                drawable.setBounds(circlesB.get(i));
                drawable.draw(canvas);
            }
        }
    }

    /**
     * Draw line between two Rect objects
     *
     * @param canvas
     * @param start
     * @param end
     */
    private void drawLine(Canvas canvas, Rect start, Rect end) {
        canvas.drawLine(start.centerX(), start.centerY(), end.centerX(), end.centerY(), pLine);
    }

    private boolean isLinkArea(MotionEvent event){
        int x = (int) event.getX();
        if (x > (leftX - 10) && x < (rightX + 10))
            return true;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                if (isLinkArea(event))
//                    QuizActivity.canScroll = false;
                if (mTouchDrawListener != null && isLinkArea(event))
                    mTouchDrawListener.onTouchDrawDown();
                connectionOrder.clear();
                setTouchPoint(event);
                for (int i = 0; i < circlesA.size(); i++) {
                    if (touchPoint.intersect(circlesA.get(i))) {
                        if (markedA.get(i))
                            removeConnection(i, true);
                        touchA = true;
                        connectionOrder.add(i);
                        return true;
                    }
                }
                for (int i = 0; i < circlesB.size(); i++) {
                    if (touchPoint.intersect(circlesB.get(i))) {
                        if (markedB.get(i))
                            removeConnection(i, false);
                        touchA = false;
                        connectionOrder.add(i);
                        return true;
                    }
                }
//                for (int i = 0; i < indexes.length; i++) {
//                    int circleNumber = indexes[i];
//                    if (touchPoint.intersect(circles[circleNumber])) {
//                        connectionOrder.add(circleNumber);
//                        return true;
//                    }
//                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                setTouchPoint(event);
                if (connectionOrder.size() <= 1) {
                    ArrayList<Rect> circ;
                    if (touchA)
                        circ = circlesB;
                    else
                        circ = circlesA;

                    for (int i = 0; i < circ.size(); i++) {
                        if (touchPoint.intersect(circ.get(i))) {
                            if ((touchA && !markedB.get(i)) || (!touchA && !markedA.get(i))) {
                                connectionOrder.add(i);
                            }
                        }
                    }
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
//                QuizActivity.canScroll = true;
                if (mTouchDrawListener != null)
                    mTouchDrawListener.onTouchDrawUP();
                touchPoint.left = 0;
                touchPoint.right = 0;
                touchPoint.top = 0;
                touchPoint.bottom = 0;

                if (connectionOrder.size() == 2) {

                    if (touchA) {
                        Connection c = new Connection(connectionOrder.get(0), connectionOrder.get(1), touchA);
                        connected.add(c);
                        markedA.set(connectionOrder.get(0), true);
                        markedB.set(connectionOrder.get(1), true);
                    } else {
                        Connection c = new Connection(connectionOrder.get(1), connectionOrder.get(0), touchA);
                        connected.add(c);
                        markedB.set(connectionOrder.get(0), true);
                        markedA.set(connectionOrder.get(1), true);
                    }
                    updateResponse();
                }

                connectionOrder.clear();
                invalidate();

                return true;
            case MotionEvent.ACTION_CANCEL:
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void removeConnection(int k, boolean A) {
        for (int i = 0; i < connected.size(); i++) {
            int index;
            if (A)
                index = connected.get(i).getIndexA();
            else
                index = connected.get(i).getIndexB();
            if (index == k) {
                markedA.set(connected.get(i).getIndexA(), false);
                markedB.set(connected.get(i).getIndexB(), false);
                connected.remove(i);
                break;
            }
        }
        updateResponse();
    }

    /**
     * Set the position of the touch point
     *
     * @param event
     */
    private void setTouchPoint(MotionEvent event) {
        touchPoint.left = (int) event.getX();
        touchPoint.top = (int) event.getY();
        touchPoint.bottom = touchPoint.top + 5;// + dp48;
        touchPoint.right = touchPoint.left + 5;// + dp48;
    }

    /**
     * Animate the widget in
     */
    public void animateIn() {
        animateIn(0);
    }

    public void animateIn(final long delay) {
        if (!isEnabled()) {
            return;
        }
        if (getVisibility() == GONE) {
            setVisibility(INVISIBLE);
        }
        if (circles[0] == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateIn(delay);
                }
            }, ANIMATION_DURATION);
            return;
        }

        List<Animator> animators = animateInFromMiddle();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.setDuration(animationType == ANIMATION_TYPE_NONE ? 0 : ANIMATION_DURATION);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setEnabled(false);
                setVisibility(VISIBLE);
                if (mPatternListener != null) {
                    mPatternListener.animateInStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEnabled(true);
                if (mPatternListener != null) {
                    mPatternListener.animateInEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.setStartDelay(delay);
        set.start();
    }

    private List<Animator> animateInFromMiddle() {
        List<Animator> animators = new ArrayList<>();


        for (int i = 0; i < circlesB.size(); i++) {
//            int circleNumber = indexes[i];
            ValueAnimator leftAnim = ValueAnimator.ofInt(circles[4].left, circlesB.get(i).left);
            leftAnim.setInterpolator(new OvershootInterpolator());
            leftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                Rect circle = null;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int left = (Integer) animation.getAnimatedValue();
                    circle.left = left;
                    circle.right = left + diameter;
                }

                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
                    circle = rect;
                    return this;
                }
            }.init(circlesB.get(i)));
            animators.add(leftAnim);

            ValueAnimator topAnim = ValueAnimator.ofInt(circles[4].top, circlesB.get(i).top);
            topAnim.setInterpolator(new OvershootInterpolator());
            topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                Rect circle = null;

                public void onAnimationUpdate(ValueAnimator animation) {
                    int top = (Integer) animation.getAnimatedValue();
                    circle.top = top;
                    circle.bottom = top + diameter;
                    invalidate();
                }

                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
                    circle = rect;
                    return this;
                }
            }.init(circlesB.get(i)));
            animators.add(topAnim);
        }
        return animators;
    }

//    private List<Animator> animateInFromBottom() {
//        List<Animator> animators = new ArrayList<>();
//        int height = getHeight();
//        if (height <= 0) {
//            height = getResources().getDisplayMetrics().heightPixels;
//        }
//        for (int i = 0; i < indexes.length; i++) {
//            int circleNumber = indexes[i];
//            int originalTop = circles[circleNumber].top;
//            circles[circleNumber].top = height;
//            circles[circleNumber].bottom = height + diameter;
//            ValueAnimator topAnim = ValueAnimator.ofInt(height, originalTop);
//            topAnim.setInterpolator(new DecelerateInterpolator());
//            topAnim.setStartDelay((ANIMATION_DURATION / indexes.length) * (i % 3));
//            topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                Rect circle = null;
//
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int top = (Integer) animation.getAnimatedValue();
//                    circle.top = top;
//                    circle.bottom = top + diameter;
//                    invalidate();
//                }
//
//                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
//                    circle = rect;
//                    return this;
//                }
//            }.init(circles[circleNumber]));
//            animators.add(topAnim);
//        }
//        invalidate();
//        return animators;
//    }

    /**
     * Animate the widget out
     */
//    public void animateOut() {
//        animateOut(0);
//    }
//
//    public void animateOut(final long delay) {
//        if (!isEnabled()) {
//            return;
//        }
//        if (circles[0] == null) {
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    animateOut(delay);
//                }
//            }, ANIMATION_DURATION);
//            return;
//        }
//
//        List<Animator> animators = (animationType == ANIMATION_TYPE_MIDDLE) ?
//                animateOutToMiddle() : animateOutToBottom();
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(animators);
//        set.setDuration(animationType == ANIMATION_TYPE_NONE ? 0 : ANIMATION_DURATION);
//        set.setStartDelay(delay);
//        set.start();
//        set.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                setEnabled(false);
//                if (mPatternListener != null) {
//                    mPatternListener.animateOutStart();
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setEnabled(true);
//                if (mPatternListener != null) {
//                    mPatternListener.animateOutEnd();
//                }
//                setupCircles();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                onAnimationEnd(animation);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//    }
//
//    private List<Animator> animateOutToBottom() {
//        List<Animator> animators = new ArrayList<>();
//        int height = getHeight();
//        if (height <= 0) {
//            height = getResources().getDisplayMetrics().heightPixels;
//        }
//        int totalLength = indexes.length;
//        for (int i = 0; i < totalLength; i++) {
//            int circleNumber = indexes[i];
//            ValueAnimator topAnim = ValueAnimator.ofInt(circles[circleNumber].top, height);
//            topAnim.setInterpolator(new AccelerateInterpolator());
//            if (animationType != ANIMATION_TYPE_NONE) {
//                topAnim.setStartDelay((ANIMATION_DURATION / indexes.length) * (i % 3));
//            }
//            topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                Rect circle = null;
//
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int top = (Integer) animation.getAnimatedValue();
//                    circle.top = top;
//                    circle.bottom = top + diameter;
//                    invalidate();
//                }
//
//                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
//                    circle = rect;
//                    return this;
//                }
//            }.init(circles[circleNumber]));
//            animators.add(topAnim);
//        }
//        return animators;
//    }
//
//    private List<Animator> animateOutToMiddle() {
//        List<Animator> animators = new ArrayList<>();
//        for (int i = 0; i < indexes.length; i++) {
//            final int circleNumber = indexes[i];
//            ValueAnimator leftAnim = ValueAnimator.ofInt(circles[circleNumber].left, circles[4].left);
//            leftAnim.setInterpolator(new DecelerateInterpolator());
//            if (animationType != ANIMATION_TYPE_NONE) {
//                leftAnim.setStartDelay((ANIMATION_DURATION / indexes.length) * i);
//            }
//            leftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                Rect circle = null;
//
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int left = (Integer) animation.getAnimatedValue();
//                    circle.left = left;
//                    circle.right = left + diameter;
//                }
//
//                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
//                    circle = rect;
//                    return this;
//                }
//            }.init(circles[circleNumber]));
//            animators.add(leftAnim);
//
//            ValueAnimator topAnim = ValueAnimator.ofInt(circles[circleNumber].top, circles[4].top);
//            topAnim.setInterpolator(new DecelerateInterpolator());
//            if (animationType != ANIMATION_TYPE_NONE) {
//                topAnim.setStartDelay((ANIMATION_DURATION / indexes.length) * i);
//            }
//            topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                Rect circle = null;
//
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    int top = (Integer) animation.getAnimatedValue();
//                    circle.top = top;
//                    circle.bottom = top + diameter;
//                    invalidate();
//                }
//
//                public ValueAnimator.AnimatorUpdateListener init(Rect rect) {
//                    circle = rect;
//                    return this;
//                }
//            }.init(circles[circleNumber]));
//            animators.add(topAnim);
//        }
//        return animators;
//    }

    /**
     * An interface for the animation events of the widget
     */
    public interface OnConnectPatternListener {
        void onPatternEntered(ArrayList<Integer> result);

        void onPatternAbandoned();

        void animateInStart();

        void animateInEnd();

        void animateOutStart();

        void animateOutEnd();

        void onViewDrawed();
    }

    public interface OnViewDrawedListener {
        void onDrawedFinish();

        void onConnectionChanged();
    }

    public interface OnTouchDrawListener {
        void onTouchDrawDown();

        void onTouchDrawUP();
    }

    /**
     * Set a callback when animations starts/ends for the widget
     *
     * @param l OnConnectPatternListener
     */
    public void setOnConnectPatternListener(OnConnectPatternListener l) {
        mPatternListener = l;
    }

    public void setOnViewDrawedListener(OnViewDrawedListener listener) {
        mDrawedListener = listener;
    }
    public void setOnTouchDrawListener(OnTouchDrawListener listener) {
        mTouchDrawListener = listener;
    }

    public void clearConnected(){
        connected = new ArrayList<>();
        invalidate();
    }
}
