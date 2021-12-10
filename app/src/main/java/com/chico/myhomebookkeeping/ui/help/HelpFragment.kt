package com.chico.myhomebookkeeping.ui.help

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.databinding.FragmentHelpBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var helpHolder:WebView
    private val localUrl = Constants.LOCAL_ASSETS_URL

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!

        helpHolder = binding.helpHolder

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireContext()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireContext()))
            .build()
        helpHolder.webViewClient = LocalContentWebViewClient(assetLoader)

        navControlHelper = NavControlHelper(control)

        when (control.previousBackStackEntry?.destination?.id) {
            R.id.nav_money_moving -> setUrl("moneyMovingFragmentHelp.html")
            R.id.nav_new_money_moving -> setUrl("newMoneyMovingFragmentHelp.html")
            R.id.nav_cash_account -> setUrl("cashAccountsFragmentHelp.html")
            R.id.nav_categories -> setUrl("categoriesFragmentHelp.html")
            R.id.nav_currencies -> setUrl("currenciesFragmentHelp.html")
            R.id.nav_setting -> setUrl("settingFragmentHelp.html")
        }
        binding.submitButton.setOnClickListener {
            presSubmitButton()
        }

    }

    private fun setUrl(url: String) {
        val finUrl = localUrl + url
        helpHolder.loadUrl(finUrl)

    }


    private fun presSubmitButton() {
        navControlHelper.moveToPreviousFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private class LocalContentWebViewClient(private val assetLoader: WebViewAssetLoader) : WebViewClientCompat() {
        @RequiresApi(21)
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        // to support API < 21
        override fun shouldInterceptRequest(
            view: WebView,
            url: String
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(Uri.parse(url))
        }
    }

}