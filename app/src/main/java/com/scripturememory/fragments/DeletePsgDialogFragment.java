package com.scripturememory.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.scripturememory.R;
import com.scripturememory.models.MemoryPassage;

public class DeletePsgDialogFragment extends DialogFragment{

    public static DeletePsgDialogFragment newInstance (String PsgRef, String PsgId) {
        DeletePsgDialogFragment frag = new DeletePsgDialogFragment();

        Bundle args = new Bundle();
        args.putString("psg_ref", PsgRef);
        args.putString("psg_id", PsgId);
        frag.setArguments(args);

        return frag;
    }

    public String getPsgRef() {
        return getArguments().getString("psg_ref");
    }

    public String getPsgId() {
        return getArguments().getString("psg_id");
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeletePassageDialogListener{
        public void onDialogDeleteClick(DialogFragment dialog, String PsgId);
        public void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DeletePassageDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the listener so we can send events to the host
            mListener = (DeletePassageDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeletePassageDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.delete_psg_dialog_title)
                .setMessage(getString(R.string.delete_psg_dialog_msg, getPsgRef()))
                .setPositiveButton(R.string.confirmation_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int id) {
                        mListener.onDialogDeleteClick(DeletePsgDialogFragment.this, getPsgId());
                    }
                })
                .setNegativeButton(R.string.confirmation_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogCancelClick(DeletePsgDialogFragment.this);
                    }
                })
                .create();
    }
}
