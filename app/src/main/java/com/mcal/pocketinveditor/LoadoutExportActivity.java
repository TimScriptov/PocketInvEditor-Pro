package com.mcal.pocketinveditor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTOutputStream;

public class LoadoutExportActivity extends Activity implements OnClickListener {
    public static final String LOADOUT_EXTENSION = ".peinv";
    private Button exportButton;
    private TextView name;

    private class ExportLoadoutTask implements Runnable {
		public File file;
		public CompoundTag tag;
		public ExportLoadoutTask(File file, CompoundTag tag) {
			this.file = file;
			this.tag = tag;
		}
		public void run() {
			boolean success = true;
			FileOutputStream fos = null;
			NBTOutputStream nos = null;
			try {
				fos = new FileOutputStream(file);
				nos = new NBTOutputStream(fos, false, true);
				nos.writeTag(tag);
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			} finally {
				try {
					if (nos != null) nos.close();
					if (fos != null) fos.close();
				} catch (Exception e) {
					e.printStackTrace();
					success = false;
				}
			}
			final boolean showSuccessMsg = success;
			runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(LoadoutExportActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
						finish();
					}
				});
		}
	}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadout_export);
        name = findViewById(R.id.loadout_name_input);
        exportButton = findViewById(R.id.loadout_export_button);
        exportButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == exportButton) {
            startExport();
        }
    }

    public void startExport() {
        File file = new File(getLoadoutFolder(this), name.getText().toString() + LOADOUT_EXTENSION);
        if (file.exists()) {
            name.setError("Loadout with same name exists");
        } else {
            new Thread(new ExportLoadoutTask(file, NBTConverter.writeLoadout(getInventoryToExport()))).start();
        }
    }

    public List<InventorySlot> getInventoryToExport() {
        if (!getIntent().getBooleanExtra("IsTileEntity", false)) {
            return EditorActivity.level.getPlayer().getInventory();
        }
        return ((ContainerTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1))).getItems();
    }

    public static File getLoadoutFolder(Activity activity) {
        File outDir = new File(Environment.getExternalStorageDirectory(), "Android/data/com.mcal.pocketinveditor/loadouts");
        outDir.mkdirs();
        return outDir;
    }
}
