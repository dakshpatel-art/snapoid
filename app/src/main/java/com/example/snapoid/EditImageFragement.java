package com.example.snapoid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.snapoid.Interface.EditImageFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditImageFragement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditImageFragement extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListener listener;
    SeekBar seekbar_brightness,seekbar_constrant,seekBar_saturation;

    public void setListener(MainActivity listener) {
        this.listener = listener;
    }

    static  EditImageFragement instance;

    public static EditImageFragement getInstance() {
        if (instance == null)
            instance = new EditImageFragement();
        return instance;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditImageFragement() {
        // Required empty public constructor
    }


    public static EditImageFragement newInstance(String param1, String param2) {
        EditImageFragement fragment = new EditImageFragement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        View itemView =  inflater.inflate(R.layout.fragment_edit_image_fragement, container, false);

        seekbar_brightness = (SeekBar)itemView.findViewById(R.id.seekbar_brightness);
        seekbar_constrant= (SeekBar)itemView.findViewById(R.id.seekbar_contraint);
        seekBar_saturation = (SeekBar)itemView.findViewById(R.id.seekbar_saturation);

        seekbar_brightness.setMax(200);
        seekbar_brightness.setProgress(100);

        seekbar_constrant.setMax(20);
        seekbar_constrant.setProgress(0);

        seekBar_saturation.setMax(30);
        seekBar_saturation.setProgress(10);

        seekBar_saturation.setOnSeekBarChangeListener(this);
        seekbar_constrant.setOnSeekBarChangeListener(this);
        seekBar_saturation.setOnSeekBarChangeListener(this);

        return itemView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener != null)
        {
            if (seekBar.getId() == R.id.seekbar_brightness)
            {
                listener.onBrightnessChanged(progress-100);

            }
            else if (seekBar.getId() == R.id.seekbar_contraint)
            {
                progress+=10;
                float value = .10f*progress;
                listener.onConstrantChanged(value);
            }
            else if (seekBar.getId() == R.id.seekbar_saturation)
            {
                float value = .10f*progress;
                listener.onSaturationChanged(value);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
              if (listener!=null)
              {
                  listener.onEditStarted();
              }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
          if (listener!=null)
          {
              listener.onEditCompleted();
          }
    }

    public void resetControls()
    {
        seekbar_brightness.setProgress(100);
        seekbar_constrant.setProgress(0);
        seekBar_saturation.setProgress(10);
    }
}