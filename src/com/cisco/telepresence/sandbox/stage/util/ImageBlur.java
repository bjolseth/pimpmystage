package com.cisco.telepresence.sandbox.stage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

// Copied from https://plus.google.com/+MarioViviani/posts/fhuzYkji9zz
public class ImageBlur {
    public Bitmap blurBitmap(Context context, Bitmap bitmap) {


    //Let's create an empty bitmap with the same size of the bitmap we want to blur
            Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);


    //Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(context);


    //Create an Intrinsic Blur Script using the Renderscript
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));


    //Create the in/out Allocations with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);


    //Set the radius of the blur
            blurScript.setRadius(25.f);


    //Perform the Renderscript
            blurScript.setInput(allIn);
            blurScript.forEach(allOut);


    //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);


    //recycle the original bitmap
            bitmap.recycle();


    //After finishing everything, we destroy the Renderscript.
            rs.destroy();


            return outBitmap;

            }
}