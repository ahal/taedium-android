package me.taedium.android.add;

import java.util.ArrayList;

import me.taedium.android.FirstStart;
import me.taedium.android.R;
import me.taedium.android.api.Caller;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AddTags extends WizardActivity {
    private static final String MODULE = "ADD_TAGS";
    private static final int DIALOG_ADD_MANUAL_TAG = 615;
    private RelativeLayout rlAddTag;
    
    private ArrayList<String> tags, manualtags;
    private String [] autotags;
    private TextView tvAutoTag, tvManualTag;
    private Button bNewTag;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_6_tags);
        
        initializeWizard(this, FirstStart.class, 0);
        data = getIntent().getExtras();

        title = this.getResources().getString(R.string.help_add_tags_title);
        helpText = this.getResources().getString(R.string.help_add_tags); 
        
        // Override the default next button behaviour
        bNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fillData();
                
                // Submit the activity to the server
                boolean success = Caller.getInstance(getApplicationContext()).addRecommendation(data);
                if (success) {
                    Toast.makeText(AddTags.this, getString(R.string.msgAddSuccess), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddTags.this, getString(R.string.msgAddFailure), Toast.LENGTH_LONG).show();
                }
                // Return back to first activity (pop other activities off stack)
                Intent i = new Intent(AddTags.this, FirstStart.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        bNext.setText("Submit");

        tags = new ArrayList<String>();
        manualtags = new ArrayList<String>();
        restoreData();
        
        rlAddTag = (RelativeLayout)findViewById(R.id.rlAddTagScrollableContent);
        tvAutoTag = (TextView)findViewById(R.id.tvAddAutoTag);
        tvManualTag = (TextView)findViewById(R.id.tvAddManualTag);

        // Add Manual Tags button
        bNewTag = (Button)findViewById(R.id.bAddNewTag);
        bNewTag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_ADD_MANUAL_TAG);
            }
        });
      
        // Get autotags from the server if we need to
        if (autotags == null) {
            autotags = getAutoGeneratedTags();
        }
        Log.i(MODULE, "Auto-generated tags: " + autotags.length);
        if (autotags.length == 0) {
            tvAutoTag.setVisibility(View.GONE);
            tvManualTag.setText(R.string.tvAddManualTagNoAuto);
        } else {
	        // Programmatically create the check boxes for the autotags
            for (int i = 0; i < autotags.length; ++i) {
    	        CheckBox box = new CheckBox(AddTags.this);
    	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
    	                                                                             LayoutParams.WRAP_CONTENT);
    	        params.addRule(RelativeLayout.BELOW, i==0 ? tvAutoTag.getId() : i);
                box.setLayoutParams(params);
    	        box.setId(i+1);
    	        box.setText(autotags[i]);
    	        box.setTextColor(Color.BLACK);
    	        for (String tag: tags) {
    	            if (tag.equals(autotags[i])) {
    	                box.setChecked(true);
    	                break;
    	            }
    	        }
    	        // add listener to add/remove the tag from the tags list
    	        box.setOnCheckedChangeListener(new TagCheckChangeListener()); 
    	        rlAddTag.addView(box);
    	        
    	        params = (RelativeLayout.LayoutParams)tvManualTag.getLayoutParams();
    	        params.addRule(RelativeLayout.BELOW, i+1);
            }
        }
        
        // Create checkboxes for previously added manual tags
        if (manualtags.size() > 0) {
            for (int i = 0; i < manualtags.size(); ++i) {
                addManualCheckBox(i);
            }
        }
        
        if (tags.size() == 0) {
            bNext.setEnabled(false);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.Dialog);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        switch(id) {
            case DIALOG_ADD_MANUAL_TAG:
                dialog.setContentView(R.layout.add_manual_tag);
                dialog.setTitle("Enter a Tag");
                final EditText etAddTag = (EditText)dialog.findViewById(R.id.etAddTag);
                Button bAdd = (Button)dialog.findViewById(R.id.bAdd);
                bAdd.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String text = etAddTag.getText().toString();
                        if (!text.equals("") && !tags.contains(text)) {
                            tags.add(text);
                            manualtags.add(text);
                            addManualCheckBox(manualtags.size() - 1);
                            bNext.setEnabled(true);
                        }
                        etAddTag.setText("");
                        Toast.makeText(AddTags.this, "Added tag: " + text, Toast.LENGTH_SHORT).show();
                    }
                });
                Button bDone = (Button)dialog.findViewById(R.id.bDone);
                bDone.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dismissDialog(DIALOG_ADD_MANUAL_TAG);
                    }
                });
                break;
            default:
                // dialog not known to this activity
                return super.onCreateDialog(id);
        }
        return dialog;
    }
    
    private String[] getAutoGeneratedTags() {
        String name = data.getString("name");
        String desc = data.getString("desc");
        return Caller.getInstance(getApplicationContext()).getRecommendedTags(name, desc);
    }
    
    /**
     * Adds a manual checkbox after the Add Manual Tags button
     * @param index
     */
    private void addManualCheckBox(int index) {
        int id = index + autotags.length;
        CheckBox box = new CheckBox(AddTags.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                                                                             LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, index==0 ? bNewTag.getId() : id);
        box.setLayoutParams(params);
        box.setId(id+1);
        box.setText(manualtags.get(index));
        box.setTextColor(Color.BLACK);
        for (String tag: tags) {
            if (tag.equals(manualtags.get(index))) {
                box.setChecked(true);
                break;
            }
        }
        // add listener to add/remove the tag from the tags list
        box.setOnCheckedChangeListener(new TagCheckChangeListener()); 
        rlAddTag.addView(box);
    }

    @Override
    protected void fillData() {
        data.putStringArrayList("tags", tags);
        data.putStringArrayList("manualtags", manualtags);
        // keep track of auto tags to prevent un-necessary api calls 
        data.putStringArray("autotags", autotags);
    }

    @Override
    protected void restoreData() {
        if (data.containsKey("tags")) {
            tags = data.getStringArrayList("tags");
        }
        if (data.containsKey("manualtags")) {
        	manualtags = data.getStringArrayList("manualtags");
        }
        if (data.containsKey("autotags")) {
            autotags = data.getStringArray("autotags");
        }
    }
    
    private class TagCheckChangeListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                tags.add(buttonView.getText().toString());
                bNext.setEnabled(true);
            } else {
                tags.remove(buttonView.getText().toString());
                if (tags.size() == 0) {
                    bNext.setEnabled(false);
                }
            }
        }
    }
}
