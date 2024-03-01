package com.example.tasklistfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.tasklistfragments.databinding.FragmentAddTaskBinding
import com.bumptech.glide.Glide

class AddTaskFragment : Fragment(R.layout.fragment_add_task) {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        with(binding) {
            buttonCreateAddTask.setOnClickListener {
                if (isCorrectData(binding)) {
                    val taskName = editTextCreateTaskName.text.toString().trim()
                    val taskImage = editTextCreateTaskImage.text.toString()

                    transferData(
                        "imageInfoFromAddTaskFragment",
                        bundleOf(Pair("taskName", taskName), Pair("taskImage", taskImage))
                    )
                    removeFragmentAndPopBackStack(this@AddTaskFragment)

                } else {
                    createAlertDialog(resources.getString(R.string.error_null_text))
                }
            }
            buttonAddImage.setOnClickListener {
                launcherGallery.launch("image/*")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isCorrectData(binding: FragmentAddTaskBinding): Boolean {
        with(binding) {
            return editTextCreateTaskName.text.toString().trim() != ""
        }
    }

    private fun createAlertDialog(text: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder
            .setMessage(text)
            .setTitle(R.string.error_title)
            .setPositiveButton(R.string.error_OK) { dialog, _ -> dialog.cancel() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun transferData(requestKey: String, bundle: Bundle) {
        setFragmentResult(requestKey, bundle)
    }

    private fun removeFragmentAndPopBackStack(fragment: Fragment) {
        parentFragmentManager.popBackStack()
        parentFragmentManager.beginTransaction().remove(fragment).commitNow()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            binding.buttonAddImage.background = null

            Glide.with(binding.buttonAddImage.context)
                .load(it)
                .circleCrop()
                .placeholder(R.drawable.ic_error_image_task)
                .error(R.drawable.ic_error_image_task)
                .into(binding.buttonAddImage)

            binding.editTextCreateTaskImage.setText(it.toString())
        }
    )

}