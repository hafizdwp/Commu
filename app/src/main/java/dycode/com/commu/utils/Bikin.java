package dycode.com.commu.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import dycode.com.commu.R;

/**
 * Created by Asus on 11/14/2017.
 */

public class Bikin {

    static Toast mToast;

    public static void toast(Context context, String message){
        if(mToast !=null) mToast.cancel();
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void glide(Context context, String target, ImageView imageView){
        Glide.with(context)
                .load(target)
                .transform(new CircleTransform(context))
                .into(imageView);
    }

    public static void glide(Context context, int target, ImageView imageView){
        Glide.with(context)
                .load(target)
                .transform(new CircleTransform(context))
                .into(imageView);
    }

//    public static void glide(Activity activity, String target, ImageView imageView){
//        Glide.with(activity).load(target).apply(new RequestOptions().circleCrop()).into(imageView);
//    }
//
//    public static void glide(View view, String target, ImageView imageView){
//        Glide.with(view).load(target).apply(new RequestOptions().circleCrop()).into(imageView);
//    }
}
