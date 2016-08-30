package com.hdfc.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Admin on 09-06-2016.
 */
public class CheckView extends ImageView {

    private final char[] charContent = new char[]{'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};
    //éªŒè¯�ç �å›¾ç‰‡
    private Bitmap bitmap = null;

    //æž„é€ å‡½æ•°
    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else {
            paint.setColor(Color.GRAY);
            paint.setTextSize(20);
            canvas.drawText("Captcha", 10, 30, paint);
        }
        super.draw(canvas);
    }

    /**
     * å¾—åˆ°éªŒè¯�ç �ï¼›è®¾ç½®å›¾ç‰‡
     *
     * @return ç”Ÿæˆ�çš„éªŒè¯�ç �ä¸­çš„æ•°å­—
     */
    public char[] getValidataAndSetImage() {
        //äº§ç”Ÿéš�æœºæ•°
        char[] strRes = generageRadom(charContent);
        //ä¼ éš�æœºä¸²å’Œéš�æœºæ•°
        bitmap = generateValidate(charContent, strRes);
        //åˆ·æ–°
        invalidate();

        return strRes;
    }


    private Bitmap generateValidate(char[] charContent, char[] charRes) {
        int width = 180, height = 100;

        int isRes = isStrContent(charContent);
        if (isRes == 0) {
            return null;
        }

        //åˆ›å»ºå›¾ç‰‡å’Œç”»å¸ƒ
        Bitmap sourceBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(sourceBitmap);
        canvas.drawColor(Color.WHITE);
        Paint numPaint = new Paint();
        numPaint.setTextSize(50);
        numPaint.setFakeBoldText(true);
        numPaint.setColor(Color.BLACK);

        //è®¾ç½®æ¯�ä¸ªå­—
        canvas.drawText(charRes[0] + "", 10, height * 3 / 4, numPaint);
        Matrix mMatrix = new Matrix();
        mMatrix.setRotate((float) Math.random() * 25);
        canvas.setMatrix(mMatrix);

        canvas.drawText(charRes[1] + "", 40, height * 3 / 4, numPaint);
        mMatrix.setRotate((float) Math.random() * 25);
        canvas.setMatrix(mMatrix);

        canvas.drawText(charRes[2] + "", 70, height * 3 / 4 - 10, numPaint);
        mMatrix.setRotate((float) Math.random() * 25);
        canvas.setMatrix(mMatrix);

        canvas.drawText(charRes[3] + "", 100, height * 3 / 4 - 15, numPaint);
        mMatrix.setRotate((float) Math.random() * 25);
        canvas.setMatrix(mMatrix);

        //è®¾ç½®ç»˜åˆ¶å¹²æ‰°çš„ç”»ç¬”
        Paint interferencePaint = new Paint();
        interferencePaint.setAntiAlias(true);
        interferencePaint.setStrokeWidth(4);
        interferencePaint.setColor(Color.BLACK);
        interferencePaint.setStyle(Paint.Style.FILL);    //è®¾ç½®paintçš„style

        //ç»˜åˆ¶ç›´çº¿
       /* int [] line;
        for(int i = 0; i < 2; i ++)
            {
            line = CheckGetUtil.getLine(height, width);
            canvas.drawLine(line[0], line[1], line[2], line[3],interferencePaint);
            }
        // ç»˜åˆ¶å°�åœ†ç‚¹
        int [] point;
        for(int i = 0; i < 100; i++)
            {
            point= CheckGetUtil.getPoint(height, width);
            canvas.drawCircle(point[0], point[1], 1, interferencePaint);
            }  */

        canvas.save();
        return sourceBitmap;
    }

    private int isStrContent(char[] charContent) {
        if (charContent == null || charContent.length <= 0) {
            return 0;
        } else {
            return 1;
        }
    }


    private char[] generageRadom(char[] charContent) {
        char[] cha = new char[4];
        // éš�æœºä¸²çš„ä¸ªæ•°
        int count = charContent.length;
        // ç”Ÿæˆ�4ä¸ªéš�æœºæ•°
        Random random = new Random();
        int randomResFirst = random.nextInt(count);
        int randomResSecond = random.nextInt(count);
        int randomResThird = random.nextInt(count);
        int randomResFourth = random.nextInt(count);

        cha[0] = charContent[randomResFirst];
        cha[1] = charContent[randomResSecond];
        cha[2] = charContent[randomResThird];
        cha[3] = charContent[randomResFourth];
        return cha;
    }

    /**
     * ä»ŽæŒ‡å®šæ•°ç»„ä¸­éš�æœºå�–å‡º4ä¸ªå­—ç¬¦(å­—ç¬¦ä¸²)
     *
     * @param strContent
     * @return
     */
    private String generageRadomStr(String[] strContent) {
        StringBuilder str = new StringBuilder();
        // éš�æœºä¸²çš„ä¸ªæ•°
        int count = strContent.length;
        // ç”Ÿæˆ�4ä¸ªéš�æœºæ•°
        Random random = new Random();
        int randomResFirst = random.nextInt(count);
        int randomResSecond = random.nextInt(count);
        int randomResThird = random.nextInt(count);
        int randomResFourth = random.nextInt(count);

        str.append(strContent[randomResFirst].toString().trim());
        str.append(strContent[randomResSecond].toString().trim());
        str.append(strContent[randomResThird].toString().trim());
        str.append(strContent[randomResFourth].toString().trim());
        return str.toString();
    }

    /**
     * ç»™å®šèŒƒå›´èŽ·å¾—éš�æœºé¢œè‰²ï¼Œæœªä½¿ç”¨
     *
     * @param rc 0-255
     * @param gc 0-255
     * @param bc 0-255
     * @return colorValue é¢œè‰²å€¼ï¼Œä½¿ç”¨setColor(colorValue)
     */
    public int getRandColor(int rc, int gc, int bc) {
        Random random = new Random();
        if (rc > 255)
            rc = 255;
        if (gc > 255)
            gc = 255;
        if (bc > 255)
            bc = 255;
        int r = rc + random.nextInt(rc);
        int g = gc + random.nextInt(gc);
        int b = bc + random.nextInt(bc);
        return Color.rgb(r, g, b);
    }
}
