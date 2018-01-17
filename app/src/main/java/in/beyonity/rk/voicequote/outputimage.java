package in.beyonity.rk.voicequote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class outputimage extends AppCompatActivity {

    DynamicSquareLayout view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outputimage);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        view =(DynamicSquareLayout) findViewById(R.id.output);
        BitmapDrawable background = new BitmapDrawable(bmp);
        view.setBackground(background);

    }
}
