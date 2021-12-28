package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentChangeFastPaymentBinding

class ChangeFastPaymentFragment : Fragment() {

    private lateinit var changeFastPaymentViewModel: ChangeFastPaymentViewModel
    private var _binding: FragmentChangeFastPaymentBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeFastPaymentBinding.inflate(inflater, container, false)
        changeFastPaymentViewModel =
            ViewModelProvider(this).get(ChangeFastPaymentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}