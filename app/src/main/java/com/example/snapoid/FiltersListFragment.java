package com.example.snapoid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapoid.Adapter.ThumbnailAdapter;
import com.example.snapoid.Interface.FilterListFragmentListener;
import com.example.snapoid.Utils.BitmapUtils;
import com.example.snapoid.Utils.SpacesItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiltersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public  class FiltersListFragment extends BottomSheetDialogFragment implements FilterListFragmentListener {

        RecyclerView recyclerView;
        ThumbnailAdapter adapter;
        List<ThumbnailItem> thumbnailItems;

       FilterListFragmentListener listener;
     static   FiltersListFragment instance;

     static Bitmap bitmap;
    public static FiltersListFragment getInstance(Bitmap bitmapSave) {
        bitmap = bitmapSave;
        if (instance==null)
        {
            instance = new FiltersListFragment();

        }
        return instance;
    }

    public void setListener(MainActivity listener) {
        this.listener = listener;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FiltersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltersListFragment newInstance(String param1, String param2) {
        FiltersListFragment fragment = new FiltersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View itemView = inflater.inflate(R.layout.fragment_filters_list, container, false);

       thumbnailItems = new ArrayList<>();
       adapter = new ThumbnailAdapter(thumbnailItems,this,getActivity());

       recyclerView = (RecyclerView)itemView.findViewById((R.id.recycler_view));
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
       recyclerView.setItemAnimator(new DefaultItemAnimator());
       int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());
       recyclerView.addItemDecoration(new SpacesItemDecoration(space));
       recyclerView.setAdapter(adapter);
       displayThumbnail(bitmap);
       recyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {

           }

           @Override
           public void onAnimationEnd(Animation animation) {

           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });

       recyclerView.addItemDecoration(new SpacesItemDecoration(space));
       recyclerView.setAdapter(adapter);

       displayThumbnail(null);


        return itemView;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thubImg;
                if (bitmap==null)
                    thubImg = BitmapUtils.getBitmapFromAssets(getActivity(),MainActivity.pictureName,100,100);
                  else
                     thubImg = Bitmap.createScaledBitmap(bitmap,100,100,false);

                  if (thubImg == null)
                      return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thubImg;
                thumbnailItem.filterName="Normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter>  filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter:filters)
                {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thubImg;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {

        if (listener != null)
            listener.onFilterSelected(filter);
    }
}