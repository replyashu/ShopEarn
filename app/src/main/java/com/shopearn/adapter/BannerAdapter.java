package com.shopearn.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shopearn.R;
import com.shopearn.global.AppController;
import com.shopearn.model.Banner;
import com.shopearn.tracker.GATracking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * Created by apple on 15/01/17.
 */

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<Banner> banners;

    private SharedPreferences sp;

    private String email;

    public BannerAdapter(Context context, List<Banner> bannerList){
        this.context = context;
        this.banners = bannerList;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public float getPageWidth(int position) {
        return 0.9f;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleDraweeView imgBanner;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.banner_item, container,
                false);

        sp = context.getSharedPreferences("user", 0);

        email = sp.getString("email", "guest");
//
        imgBanner = (SimpleDraweeView) itemView.findViewById(R.id.bannerImage);
        try {
            final String imgUrl;
            final String link;

            Uri uri = Uri.parse(banners.get(position).getImage());

            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageRequest.getSourceUri())
                    .setAutoPlayAnimations(true)
                    .build();
            imgBanner.setController(controller);
            link = banners.get(position).getLink();



            imgBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    GATracking.trackClickEvents(context, link);

                    if(link.contains("flipkart")){

                        String extraParams = "&affExtParam1=" + AppController.getInstance().getAndroidId()
                                + "&affExtParam2=" + email;
                        PackageManager manager = context.getPackageManager();
                        try {
                            Intent i = manager.getLaunchIntentForPackage("com.flipkart.android");
                            if (i == null) {
                                throw new PackageManager.NameNotFoundException();
                            }
                            i.addCategory(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link + extraParams));
                            context.startActivity(i);

                        } catch (PackageManager.NameNotFoundException e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://affiliate.flipkart.com/install-app?affid=ashuinbit"+ extraParams)));
                        }
                    }
                    else{
                        if(link.contains("amazon")) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(link + AppController.getInstance().getAndroidId() + "~~email~~" + email)));
                            } catch (ActivityNotFoundException e) {
//                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=in.amazon.mShop.android.shopping")));
                            }
                        }
                    }


//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }
            });
            ((ViewPager) container).addView(itemView);
        }
        catch (Exception e){

        }

        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
