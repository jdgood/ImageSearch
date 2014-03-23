package com.codepath.imagesearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class FilterDialog extends DialogFragment {
	private ArrayAdapter<CharSequence> sizeAdapter;
	private ArrayAdapter<CharSequence> colorAdapter;
	private ArrayAdapter<CharSequence> typeAdapter;
	private Spinner spSize;
	private Spinner spColor;
	private Spinner spType;
	
	private EditText etSite;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.filter_dialog, null);
		setupViews(v);
		
		builder.setView(v);
		builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					//apply filters, rerun search
					((MainActivity)getActivity()).size = (String)spSize.getSelectedItem();
					((MainActivity)getActivity()).color = (String)spColor.getSelectedItem();
					((MainActivity)getActivity()).type = (String)spType.getSelectedItem();
					((MainActivity)getActivity()).site = (String)etSite.getText().toString();
					((MainActivity)getActivity()).onSearch(getView());
				}
			});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					FilterDialog.this.getDialog().cancel();
				}
			});
		
		return builder.create();
	}
	
	private void setupViews(View v) {
		setupSize(v);
		setupColor(v);
		setupType(v);
		setupSite(v);
	}
	
	private void setupSize(View v) {
		spSize = (Spinner) v.findViewById(R.id.spSize);
		sizeAdapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.sizes_array, android.R.layout.simple_spinner_item);
		sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSize.setAdapter(sizeAdapter);
		spSize.setSelection(sizeAdapter.getPosition(((MainActivity)getActivity()).size));
	}
	
	private void setupColor(View v) {
		spColor = (Spinner) v.findViewById(R.id.spColor);
		colorAdapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.colors_array, android.R.layout.simple_spinner_item);
		colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spColor.setAdapter(colorAdapter);
		spColor.setSelection(colorAdapter.getPosition(((MainActivity)getActivity()).color));
	}
	
	private void setupType(View v) {
		spType = (Spinner) v.findViewById(R.id.spType);
		typeAdapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.types_array, android.R.layout.simple_spinner_item);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(typeAdapter);
		spType.setSelection(typeAdapter.getPosition(((MainActivity)getActivity()).type));
	}
	
	private void setupSite(View v) {
		etSite = (EditText) v.findViewById(R.id.etSite);
		etSite.setText(((MainActivity)getActivity()).site);
		etSite.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					return true;
				}
				return false;
			}
		});
	}
}
