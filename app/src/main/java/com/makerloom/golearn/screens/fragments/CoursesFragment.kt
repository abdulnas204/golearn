package com.makerloom.golearn.screens.fragments

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.makerloom.golearn.R
import com.makerloom.golearn.adapters.GCourseAdapter
import com.makerloom.golearn.models.Course
import com.makerloom.golearn.screens.HomeActivity
import mehdi.sakout.fancybuttons.FancyButton
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CoursesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CoursesFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CoursesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var courseRV: RecyclerView? = null

    private var receiveBtn: FancyButton? = null

    private var courses: List<Course>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    fun getCourses (): List<Course> {
        val courses = ArrayList<Course>()

        val extDir = Environment.getExternalStorageDirectory()

        val coursesDir = File(extDir, "GoLearn/Courses")
        Log.d(TAG, "$coursesDir")

        coursesDir.list { dir: File, name: String ->

            val courseCode = name.split(" - ")[0]
            val courseName = name.split(" - ")[1]

            Log.d(TAG, "$courseCode, $courseName")

            courses.add(Course(courseCode, courseName, File(dir, name).absolutePath))
        }

        return courses
    }

    private var hasStoragePermission = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Dexter.withActivity(this@CoursesFragment.activity)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report != null && report.areAllPermissionsGranted()) {
                            hasStoragePermission = true
                            courses = getCourses()
                            initViews(view)
                        }
                    }
                }).check()
    }

    fun initViews (view: View) {
        if (hasStoragePermission) {
            val manager = LinearLayoutManager(this@CoursesFragment.context)
            val adapter = GCourseAdapter(this@CoursesFragment.context, courses)

            courseRV = view.findViewById(R.id.recycler_view)
            courseRV?.apply {
                this.adapter = adapter
                layoutManager = manager
            }

            val emptyMessage = view.findViewById<TextView>(R.id.empty_message)
            if (null == courses || courses!!.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
                courseRV?.visibility = View.GONE
            }
        }

        receiveBtn = view.findViewById(R.id.receive_btn)
        receiveBtn?.apply {
            visibility = View.VISIBLE
            // setText("RECEIVE MATERIAL")
            setOnClickListener {
                (this@CoursesFragment.activity as HomeActivity).receiveFiles(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (this.activity as HomeActivity).setTitle(getString(R.string.title_courses))
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CoursesFragment.
         */

        val TAG = CoursesFragment::class.java.simpleName

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
                CoursesFragment().apply {
                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
