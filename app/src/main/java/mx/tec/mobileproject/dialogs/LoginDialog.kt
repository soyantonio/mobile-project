package mx.tec.mobileproject.dialogs

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import mx.tec.mobileproject.R
import mx.tec.mobileproject.helpers.DataBaseHelper

class LoginDialog : DialogFragment() {
    private lateinit var userName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var closeButton: TextView
    private lateinit var newUserButton: TextView
    private lateinit var progressBar: ProgressBar

    private var isNewUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_dialog, container, false)
        initGlobalVariables(view)
        return view
    }

    private fun initGlobalVariables(rootView: View) {
        userName = rootView.findViewById(R.id.login_dialog_user_name_input)
        email = rootView.findViewById(R.id.login_dialog_email_input)
        password = rootView.findViewById(R.id.login_dialog_password_input)
        loginButton = rootView.findViewById(R.id.login_dialog_login_button)
        newUserButton = rootView.findViewById(R.id.login_dialog_sign_up_button)
        closeButton = rootView.findViewById(R.id.login_dialog_close_button)
        progressBar = rootView.findViewById(R.id.login_dialog_progress_bar)
        manageLoginButton()
    }

    private fun manageLoginButton() {
        loginButton.setOnClickListener {
            val canLogin = email.text.isNotBlank() && password.text.isNotBlank() && (if (isNewUser) userName.text.isNotBlank() else true)
            if (canLogin) {
                setLoadingLogic(true)
                loginButton.setOnClickListener(null)
                DataBaseHelper(context, object: DataBaseHelper.DataBaseInterface {
                    override fun onSuccess() {
                        dismiss()
                    }

                    override fun onError() {
                        setLoadingLogic(false)
                        if (isNewUser.not()) {
                            newUserButton.visibility = View.VISIBLE
                        }
                        manageLoginButton()
                    }

                }).apply {
                    if (isNewUser) {
                        signUpWithUsernameAndPassword(userName.text.toString(), email.text.toString(), password.text.toString())
                    } else {
                        loginWithUsernameAndPassword(email.text.toString(), password.text.toString())
                    }
                }
            }
        }
        newUserButton.setOnClickListener {
            isNewUser = true
            manageNewUserLogic()
        }
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun manageNewUserLogic() {
        loginButton.text = getString(R.string.sign_up)
        newUserButton.visibility = View.GONE
        userName.visibility = View.VISIBLE
    }

    private fun setLoadingLogic(isLoading: Boolean = false) {
        userName.isEnabled = isLoading.not()
        email.isEnabled = isLoading.not()
        password.isEnabled = isLoading.not()
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        closeButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        newUserButton.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val TAG = "LoginDialog"

        fun newInstance(): LoginDialog {
            val args = Bundle()
            
            return LoginDialog().apply {
                arguments = args
            }
        }
    }
}