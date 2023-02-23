package com.example.app1

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.content.ActivityNotFoundException
import android.net.Uri
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.FragmentManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GrabUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GrabUserInfoFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var thumbnail: Bitmap? = null
    private var displayIntent: Intent? = null

    var firstNameField: EditText? = null
    var middleNameField: EditText? = null
    var lastNameField: EditText? = null
    var nextButton: Button? = null
    var pictureButton: Button? = null

    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("creating fragment")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_grab_user_info, container, false)

        firstNameField = view.findViewById<View>(R.id.first_name_et) as EditText
        middleNameField = view.findViewById<View>(R.id.middle_name_et) as EditText
        lastNameField = view.findViewById<View>(R.id.last_name_et) as EditText
        nextButton = view.findViewById(R.id.next_bt) as Button
        pictureButton = view.findViewById(R.id.picture_bt) as Button

        firstNameField!!.setText(viewModel.firstName)
        middleNameField!!.setText(viewModel.middleName)
        lastNameField!!.setText(viewModel.lastName)
        if (viewModel.thumbnailUrl != "") {
            displayImage(viewModel.thumbnailUrl, view)
        }
        nextButton!!.isEnabled = !(firstNameField!!.text == null ||
                                   middleNameField!!.text == null ||
                                   lastNameField!!.text == null ||
                                   viewModel.thumbnailUrl == "")

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                ;
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ;
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.firstName = firstNameField!!.text.toString()
                viewModel.middleName = middleNameField!!.text.toString()
                viewModel.lastName = lastNameField!!.text.toString()
                nextButton!!.isEnabled = !(firstNameField!!.text == null ||
                                            middleNameField!!.text == null ||
                                            lastNameField!!.text == null ||
                                            viewModel.thumbnailUrl == "")
            }
        }

        firstNameField!!.addTextChangedListener(textWatcher)
        middleNameField!!.addTextChangedListener(textWatcher)
        lastNameField!!.addTextChangedListener(textWatcher)
        nextButton!!.setOnClickListener(this)
        pictureButton!!.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.next_bt -> {
                val displayFragment = DisplayUserInfoFragment()
                loadFragment(displayFragment)
            }
            R.id.picture_bt -> {
                //The button press should open a camera
                print("launching camera")
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try{
                    cameraLauncher.launch(cameraIntent)
                }catch(ex:ActivityNotFoundException){
                    //Do something here
                }
            }
        }
    }

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            thumbnail = extras!!["data"] as Bitmap?

            if (canWrite) {
                val filePath = saveImage(thumbnail)
                println(filePath)
                viewModel.thumbnailUrl = filePath
                displayImage(viewModel.thumbnailUrl, requireView())
            } else {
                Toast.makeText(activity, "Count not write to external storage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayImage(imageUri: String?, v: View): Unit {
        var thumbnailImageView: ImageView? = v.findViewById(R.id.image_holder_iv) as ImageView

        val thumbnail = BitmapFactory.decodeFile(imageUri)
        if (thumbnail != null) {
            thumbnailImageView!!.setImageBitmap(thumbnail)
        }

        nextButton!!.isEnabled = !(firstNameField!!.text == null ||
                middleNameField!!.text == null ||
                lastNameField!!.text == null ||
                viewModel.thumbnailUrl == "")
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(activity, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val canWrite: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    private fun loadFragment(fragment: Fragment): Unit {
        viewModel.fragmentToLoad = 1
        val fTrans = parentFragmentManager.beginTransaction()
        fTrans.replace(R.id.info_fragment_container, fragment, "info_frag")
        fTrans.commit()
    }
}