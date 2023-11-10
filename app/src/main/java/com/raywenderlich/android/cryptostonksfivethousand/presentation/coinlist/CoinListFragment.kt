/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.cryptostonksfivethousand.presentation.coinlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.cryptostonksfivethousand.R
import com.raywenderlich.android.cryptostonksfivethousand.databinding.FragmentCoinListBinding
import com.raywenderlich.android.cryptostonksfivethousand.domain.model.NetworkUnavailable
import com.raywenderlich.android.cryptostonksfivethousand.presentation.CoinsSharedViewModel
import com.raywenderlich.android.cryptostonksfivethousand.presentation.CoinsSharedViewModelFactory
import com.raywenderlich.android.cryptostonksfivethousand.presentation.coinhistory.CoinHistoryFragment
import com.raywenderlich.android.cryptostonksfivethousand.presentation.coinlist.adapter.CoinAdapter

class CoinListFragment : Fragment() {

  private val binding get() = _binding!!
  private val viewModel: CoinListFragmentViewModel by viewModels { CoinListFragmentViewModelFactory }

  private var _binding: FragmentCoinListBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentCoinListBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupUI()
    requestCoinList()
  }

  private fun setupUI() {
    setupToolbar()
    val adapter = createAdapter()
    setupRecyclerView(adapter)
    observeViewStateUpdates(adapter)
    subscribeToViewEffects()
    subscribeToSharedViewEffects()
  }

  private fun setupToolbar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = getString(R.string.app_name)
    }
  }

  private fun createAdapter(): CoinAdapter {
    return CoinAdapter { coinId, name -> navigateToHistory(coinId, name) }
  }

  private fun navigateToHistory(coinId: String, name: String) {
    val bundle = bundleOf(
        CoinHistoryFragment.COIN_ID to coinId,
        CoinHistoryFragment.COIN_NAME to name
    )

    parentFragmentManager.commit {
      setReorderingAllowed(true)
      replace<CoinHistoryFragment>(R.id.fragment_container, args = bundle)
      addToBackStack(null)
    }
  }

  private fun setupRecyclerView(coinAdapter: CoinAdapter) {
    binding.coinsRecyclerView.apply {
      adapter = coinAdapter

      addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }
  }

  private fun observeViewStateUpdates(adapter: CoinAdapter) {
    viewModel.viewState.observe(viewLifecycleOwner) { updateUi(it, adapter) }
  }

  private fun updateUi(viewState: CoinListFragmentViewState, adapter: CoinAdapter) {
    adapter.submitList(viewState.coins)
    binding.loadingProgressBar.isVisible = viewState.loading
  }

  private fun subscribeToViewEffects() {
    // Handle view effects
  }

  private fun subscribeToSharedViewEffects() {
    // handle shared view effects
  }

  private fun handleFailure(cause: Throwable) {
    binding.loadingProgressBar.isInvisible = true

    val message = when (cause) {
      is NetworkUnavailable -> getString(R.string.network_unavailable_error_message)
      else -> getString(R.string.generic_error_message)
    }

    showSnackbar(message)
  }


  private fun showSnackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
  }

  private fun requestCoinList() {
    viewModel.requestCoinList()
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}