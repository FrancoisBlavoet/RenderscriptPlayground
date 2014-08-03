package com.codecraft.renderscriptplayground;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Matrix4f;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptGroup;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicColorMatrix;
import android.support.v8.renderscript.ScriptIntrinsicLUT;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MyActivity extends Activity {

    private static RenderScript mRenderScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.rs_imageview);
            /*imageView.setImageResource(R.drawable.photo1);*/


            mRenderScript = RenderScript.create(this.getActivity());

            Bitmap bitmap = groupBlurKernelMultColor();
            imageView.setImageBitmap(bitmap);
            return rootView;
        }


        private void testSingle() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);

            RenderScript mRenderScript = RenderScript.create(this.getActivity());
            ScriptIntrinsicBlur scriptBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            scriptBlur.setRadius(25f);
            ScriptIntrinsicColorMatrix scriptColor = ScriptIntrinsicColorMatrix.create(mRenderScript, Element.U8_4(mRenderScript));

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());


            scriptColor.setColorMatrix(new Matrix4f(
                    new float[]{1, 0, 1, 0,
                            0, 1, 1, 0,
                            0, 0, 1, 0,
                            0, 0, 0, 1}
            ));

            scriptColor.forEach(input, output);




           /* scriptBlur.setInput(input);
            scriptBlur.forEach(output);
            */
            mRenderScript.finish();
            output.copyTo(outBitmap);


            // imageView.setImageBitmap(outBitmap);
        }

        private Bitmap blur() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);

            ScriptIntrinsicBlur scriptBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            scriptBlur.setRadius(25f);

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);
            scriptBlur.setInput(input);
            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());

            scriptBlur.forEach(output);
            output.copyTo(outBitmap);

            return outBitmap;
        }

        private Bitmap lut() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);

            ScriptIntrinsicLUT lutScript = ScriptIntrinsicLUT.create(mRenderScript, Element.U8_4(mRenderScript));
            //lutScript.
            return null;
        }

        private Bitmap color(Bitmap bitmap) {

            if (bitmap == null ) {
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lenna);
            }
            ScriptIntrinsicColorMatrix scriptColor = ScriptIntrinsicColorMatrix.create(mRenderScript, Element.U8_4(mRenderScript));

            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);

            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());


            scriptColor.setColorMatrix(new Matrix4f(
                    new float[]{1, 0, 0.1f, 0,
                                0, 1, 0.1f, 0,
                                0, 0, 1, 0,
                                0, 0, 0, 1}
            ));
            scriptColor.forEach(input, output);


            output.copyTo(outBitmap);

            return outBitmap;

        }


        private Bitmap multiplyKernel(Bitmap bitmap) {

            if (bitmap == null ) {
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lenna);
            }
            ScriptC_multiply script = new ScriptC_multiply(mRenderScript);
            script.set_darkenFactor(0.2f);

            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);

            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());

            script.forEach_multiply(input, output);

            output.copyTo(outBitmap);

            return outBitmap;

        }

        private Bitmap colorKernel(Bitmap bitmap) {

            if (bitmap == null ) {
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lenna);
            }
            ScriptC_color script = new ScriptC_color(mRenderScript);
            script.set_mixFactor(0.4f);
            int color = Color.CYAN;
            script.set_blue(Color.blue(color));
            script.set_red(Color.red(color));
            script.set_green(Color.green(color));
            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);

            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());

            script.forEach_color(input, output);


            output.copyTo(outBitmap);

            return outBitmap;

        }


        private Bitmap groupKernelMultColor() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
            ScriptC_multiply scriptCMultiply = new ScriptC_multiply(mRenderScript);
            ScriptC_color scriptColor = new ScriptC_color(mRenderScript);

            scriptColor.set_mixFactor(0.4f);
            int color = Color.YELLOW;
            scriptColor.set_blue(Color.blue(color));
            scriptColor.set_red(Color.red(color));
            scriptColor.set_green(Color.green(color));

            scriptCMultiply.set_darkenFactor(0.6f);

                final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);

            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());





            ScriptGroup.Builder b = new ScriptGroup.Builder(mRenderScript);
            b.addKernel(scriptCMultiply.getKernelID_multiply());
            b.addKernel(scriptColor.getKernelID_color());
            b.addConnection(input.getType(), scriptCMultiply.getKernelID_multiply(), scriptColor.getKernelID_color());
            ScriptGroup group = b.create();

             group.setInput(scriptCMultiply.getKernelID_multiply(),input);
            group.setOutput(scriptColor.getKernelID_color(), output);


            group.execute();
            output.copyTo(outBitmap);
            return outBitmap;
        }

        private Bitmap groupBlurKernelMultColor() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
            ScriptC_multiply scriptCMultiply = new ScriptC_multiply(mRenderScript);
            ScriptC_color scriptColor = new ScriptC_color(mRenderScript);
            ScriptIntrinsicBlur scriptBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            scriptBlur.setRadius(8f);

            scriptColor.set_mixFactor(0.4f);
            int color = Color.CYAN;
            scriptColor.set_blue(Color.blue(color));
            scriptColor.set_red(Color.red(color));
            scriptColor.set_green(Color.green(color));

            scriptCMultiply.set_darkenFactor(0.6f);

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);

            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());


            ScriptGroup.Builder b = new ScriptGroup.Builder(mRenderScript);
            b.addKernel(scriptBlur.getKernelID());
            b.addKernel(scriptCMultiply.getKernelID_multiply());
            b.addKernel(scriptColor.getKernelID_color());
            b.addConnection(input.getType(), scriptBlur.getKernelID(), scriptCMultiply.getKernelID_multiply());

            b.addConnection(input.getType(), scriptCMultiply.getKernelID_multiply(), scriptColor.getKernelID_color());
            ScriptGroup group = b.create();
            scriptBlur.setInput(input);
            group.setInput(scriptBlur.getKernelID(),input);
            group.setOutput(scriptColor.getKernelID_color(), output);


            group.execute();
            output.copyTo(outBitmap);
            return outBitmap;
        }



        private Bitmap groupBlurAndColor() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);

            ScriptIntrinsicBlur scriptBlur = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            scriptBlur.setRadius(5f);

            ScriptIntrinsicColorMatrix scriptColor = ScriptIntrinsicColorMatrix.create(mRenderScript, Element.U8_4(mRenderScript));

            final Allocation input = Allocation.createFromBitmap(mRenderScript, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT | Allocation.USAGE_SHARED);
            scriptBlur.setInput(input);
            Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());


            scriptColor.setColorMatrix(new Matrix4f(
                    new float[]{1, 0f, 0f,   0,
                                1, 1,  0f,   0,
                                1, 0f, 1,    0,
                                0, 0,  0,    1}
            ));


            ScriptGroup.Builder b = new ScriptGroup.Builder(mRenderScript);
            b.addKernel(scriptBlur.getKernelID());
            b.addKernel(scriptColor.getKernelID());
            b.addConnection(input.getType(), scriptBlur.getKernelID(), scriptColor.getKernelID());
            ScriptGroup group = b.create();

           // group.setInput(scriptBlur.getKernelID(),input);
            group.setOutput(scriptColor.getKernelID(), output);


            group.execute();
            output.copyTo(outBitmap);
            return outBitmap;

        }

    }
}
