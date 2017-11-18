package com.example.ubclaunchpad.launchnote.photoBrowser

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ubclaunchpad.launchnote.R
import com.example.ubclaunchpad.launchnote.database.PicNoteDatabase
import com.example.ubclaunchpad.launchnote.recyclerView.DisplayImage
import com.example.ubclaunchpad.launchnote.recyclerView.DataAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Fragment displaying all photos in a grid format
 */
class AllPhotosFragment : Fragment() {

    private val android_version_names = arrayOf(
        "Unit 1",
        "Unit 2",
        "Unit 3",
        "Unit 4",
        "Unit 5",
        "Unit 6",
        "Unit 7",
        "Unit 8",
        "Unit 9"
    )

    private val android_image_urls = arrayOf(
        "https://lh3.googleusercontent.com/t1hTn4Zhy8sWogBzLHvZmb9uQF5qTYkicLP2jKw7jYbpe1WSoPW3NWVcwIvDNO7-0DpKcwTya7NuGz_ky2RVWexF-ykHRi2JWORnlkfA2vN1kl28YYrmztZC4L5ggc6MCUrITa4r7MBkSTHkaExhr-adkYfGaCPAA0kucOVgVkdZBlZmi_LjuPqDTwO2IrXhpz-yX1ifvTsPve2qXl8WMRn6O_NYv2CqvQQJi7sTPOePrSllW8RMvzN64DcfPBo-g7mKEEtIFygjPYEOIBiOU__Nv-GfS_YL-lakVuUP6ruhf-5mtt8vh0dbMgro2KEXWUBOJX9KWCaXbg6D-sM5HdjRoifyts6bmyzN8y1zQBBz8PrtJVsQr2eXW83G4OkgVIB-6H0irU_RM8t5KRoD2TGp88g4Zb3QRUDfOFAKFMCzpDbpqaPC2DSWY_5GEtG4NfKDqV6j-MqdgF2bpFXik98_jh1_IQnkxUbmyv2_zmwlVM1l4Ph7AXBACb0DlXsLqFNDSWb4mr-HNsjWF6-ApkkGiTO7WbuarbdJx8p8iOa8vfAlMqX_Sa7O2EtufSb6YIzWzlyRAURb-seztmQFpa33Np0M9qsTJhMApwDMrw=w750-h923-no",
        "https://lh3.googleusercontent.com/cxUZpVAQIptJeFCZVbGFkzHI7xYrXnIRkmm82j5baI_js7aE1TX26pCX8Wm-w63h-ht0-8sNPi9ghbews8M4cDOeoFdWr7I67xs9YubBim-PXR-A7iAPAQuQ9UUsYSEHtRDUIIZczhe4BMfbIyUIGE4tnmTPgcWIHr8QMQrS3bTogyJjQJWUFcAb79LD0nn-AcnhhkkiHx_KElW878xLPa7FIRhY0ha7bZJhTBkM2TN1SXBi5uTkRTsy8uSvgiwCag3jTKZ6snzTb3_ZBVIvYgFhrkztR2tEYE2W__-cyaCkNJ86Va6CfWGsoRkSIPTg6wrnhkpdXZdzw_4nMmBOws4oeAPq6Ayz7CEUw-PwtEZS8oH5RZcWrbtcLgbXGB7jqF7tt5G2ciQm4pNQBoo2lvcadXequUmn7UCvzrdEbMqHHNtuHF8MWKNoQwK2EgLS-9OP1pO-jnH9U2k8Dpy2aG_Qh8T6jVOQsMg5OKsht1cjG35SDNzByxJlZ7APZpm_wy9BrKXb7TGAvTRHWmczF2Y60Y-Wp6vv6NH8aQsmeVrj6Ze1NiTEwtPmHOt3uV-gcnV0ouVZk_xN2VvxKeA_1qsB2GhAPBAx0vWc_fT3xA=w1206-h923-no",
        "https://lh3.googleusercontent.com/iI3Fe5LCUTmikVeLqY8ufrUBXaCkLn83FzcgozF1lIijB1u6fZ0_AypJ08Aa9BOYX77BABjGrUNsN2iGIY3ikxzQfgatBPQQVXo5utzCzb__IUnZfpijT6xPicwMPiQARVh_p61I7-IECeF8BRM8u1j5yC3SlTmP8HFBsLHVK8z___iqS6KgnGKgbzwsQpjADKw0FNzEyFyZw8yekV4wasHQYw92_J_D_hiEl9x77wqieh3H5KsskAtL02Aya6y1GBYAYJyx-zRO01U_Ilu-fFGsGAXCd0Ho30qhTCFekZGMLCi8UST4e5Nez2GCsd9NKRMz0vuXfqLgCKmZn02vwIBUFjF4bxr74sK5ZBUi0EwlLp1tJWvBMqFizZrfPbDd6zyetka3tjBHsPvnFWJzL-gbvCIgLx4x7lCx1mXNW4BD0U6VO__XLhW-9GNOdP7hQi4AGYYJ2SORF-c2ZMXl-_jThvbARiAUAyNfUHpOcu1s-llUtBr2Xdj_Qn_kVJaafEMWLB_r1e0jRM4WiV0OJyF3sFTDjI7zbOjG-gMjgVSAPv_Wp8fTFlzQzSOknhcRO6RJ3621WJ04Q5W2Fv4XWqhuuc_08t98xxZf55lF0A=w815-h923-no",
        "https://lh3.googleusercontent.com/ONWAIrgrrEdiK8Pkl2g-XT61OH8QKtNhCAesmmUa0np0NcILt0kgiSfY_QF6oPp0c9hSenuq6Tl_U_TdVPw33BGzYdp86olS9doMEYdI9fJoiy8E8oocfcfShjOZ-EfTjuZIXBYvDluqXDEfyFHzPczsznL3J5XXALaeFVHaQwLfRVv7t-eQdhGQBsZIeC-Vz5imDswuCjo62MhYfbCSxbjEdOm6MY9QTAfxCW6QKqVktkAczVYMfgI1fwk-uloYUNzbdux5j1YSNzYydPyc6Ui7j4ulCG4yG6mdJdd4fFv4R2CXetCXXsCaywDl0OdQGBP59-wH-0JhJH9vuqMgtpaZGeFb0Ie2qA1_07JladuqFykD9XscvPUCndiPBKLBsTueUsTQzLVWf1cZ6ZUvMk9uXSWcvyqLIaKjlV5wX5cbpNLVn29HPKz8ja9P3uGHXQuDk-m2zJj7FYgioK76VIhKGllZ1iyt7XXmCSGze_k3tbaVgxdTXUS03cF2r7VzGtXvDi78Pvx6z5Dkb8PtWe4k8g_gHKudsSHXN3psKfj_DEW36EOSJkvRo5nAVzIfnmS3jNDOBeHYNJUF-mLe_OQ3fLhkRmKtmhP6WX8ryA=w747-h923-no",
        "https://lh3.googleusercontent.com/3i4_2iUkFBw2ncFoNRGxv8_MZF0vBFirS0RmOOdl4vW_3o59X26k9WOut4QHGth611BJw6Nck1EApvrnEX8NoFJkvi8I51V25YVKb0Sh6WpX8L-KAz_4qokJkIjMnl-sAFVdc_9Q6rVre4_KVvA-Ac0zX5P-Qw8vseZ3bJpaDqdDLDOVd37Hj5m_1MxMuXXQNrCJ4EGLbn7NMiihN4Tf0rvO7wXYSEX37e12PhZWb8UjYkyz9MOzLlRrv1sNpyWNsF7RI_c3YHn4cbl3LwIvYJtR-fuyGXJW_hu-N-22dlq9WzDVs_KVhchyqHWh2O5PlLnssM8IGCJ6QOm_9W1icMq2AlYX7wyVC4FaVsVVJVQvdIIFqcILncpPuoUwIgNiLOv0tRitoKFMFHhta1L_uqWHWuMY16YAte9SxoBm8qdBOEabd31Dp6IbSWnfg-v1Q3PGnX2C792Iu9Jd7-8VYfCmLQUiWy_0wPuMnPLcbJv8fEzkDvAxfpUmOB413R10h1RbvN-nzznNNZNG2HukvDgHlnkfJTFcrZGvm_Oh02nGf-F53OBFgNOIYOgxZ08bLCTYGiqfE5kDOcitowpC5BWvxHgzsaPs8-obkVevZA=w1130-h923-no",
        "https://lh3.googleusercontent.com/ZWiL_NLrZIrK0gQY7yS9cXb5OdA97VGWkSl-vk-nXRtrr3p1PPJMGXOAkhULZLHvY_78MGMx2mcJGQALiBbag5GgdVHeD5rH-6_n4G3qAWQedHtbe4hv7BScUXgeVF8EsagWQzShfQB9uzjXb8k1YrkfL3M_9eKgYoxexgbpEhsZFU6MlRdctc7LFfhlMJm2pdwsdxWWVV2jLDVFa_pqiNUpk3h5Ldp-RFqrkcXsmNAF5WIsahgCco792tHXvVvao1z-xAIAgIhfXY5e_0KuUMFHYlTagrFVolRvMCuOIOK4fwxR-unhzYy1KmZbBIE7Yg8ZOaaqKFRTSsj-dQ_KlSpNluoCaPj0KVE5Fei3INrAaTv1zNoaUXlwinWCiATN78BfSKPnsxdo8FalLWDxnbiQ42xMnJzJNb9GbP0M8sLJWoa6aInid2Mu3RQmhN3YQ8JTL0a1SaePiyrX1g9glDAs_YQTbpu-QDzPMnodrJvwDR9vkiQ4WVRGgYOjuSoMEgY3RKZ2Dw5VJEKlwmjDT6ZZ3-veje4_aZQi0RjeEeBeMZKzKV0dDKUzVXOkoQgeECZvxaQuYTXmyIExVoHpCo5bbiqpIdS0719Z2iQJYw=w762-h923-no",
        "https://lh3.googleusercontent.com/xt9VRukTua36-Y9n96d_mahtNWiPhzX1bAm4LjM0O8iBGUpgub2mS9MvfLAyncfn_EnuCJAU8evsUA7d0-WpieuoLrFTwUogLZlMUTSbd9N6S8Km0S-37Wu_TsNu6tGjFvtKwFyMChnl1GFKxtHaF_AzCKtRH6PHC0MpiSYfdK0387uqlP_DccssjWsv1jp_xTEvuNsfBrfv5lfw9Cd32CfUIPbFkKlMdmoOSnZx2e1hiyGasNaSgoPDI5Dd1OFpLJ30fVAZwMI1Rrn35aw51NCVhVOHLrsdkPTg8vO7mYQGLqHVP905wj9c5DuocgswPtXxLqdJug_NQrSHllUbvbT8CCTUI5uVfMtg9RaXs_PyJWbFeGpPA3vKjSJY-cCLoBm_dcC8y6WLDCKI1hObzu-T_MSiKwSg_9kPtrzBBHzS83vRguxIB8FMq8arIN67ofNippAdQZtOOoi8NbTHzBwS-8LTxBhSWI5_ATri6MzZQN0H6tOonniSwvHNUogDwvjpQDSzGX6vPEK7IGLNKQLTykeHdCX25bdlQsKx5Z6oCkp58P3dbHhcsTKZ3uxlTM0CurvOnt7VdP7LzvSqJUGcrOTnFluv0xMhy1gWug=w647-h924-no",
        "https://lh3.googleusercontent.com/yF9QqXskZqMCneu5lJv5I-Qeip96eAt9coWuwwT7quz5WSG5gNN3IqXi1yKUL4FSRi96zBpbhBkdw2Qvz-NMePWF02RR2ygAy7StvzrgBbD2YVx8zYG1a9Ynhi5x20aa0cfYiEarbMjqLQPFHCRu3f0Qc3Sfwzm4D-A5qDPxkI671cElKm8UxZSYhP8W0zcmDIHt_-wCT6IkvAcOjNelRvBOOduYBFRq7FmmR-FnMjZ3Kg6ith1aJSN_B3wvztVaIw4tr8Exgqb8zJblsQjoFQNwjXoiB6E1ysHRnYVqi-g7w43j_nShOuwumA1jz2Tl_mJfBgtcAF9SacDTa-ajty912QqasA4nXUIGZqotxW5TLKhDUx_rEovsCFvSf1_skpfnhrKyLsL8GdtVbNUc7aTK2VEZbL2BStWKn1gCGmRbZWCPATZufqg1nCXOYm-7G7qtmIST5O4oPM52iKUhneLriu8VJnUVhnZBcId7BqV1QdJybXUi1kHVc7Sl0yNaJ39NsFnhUjIL7vcvi8Nz0QLCZlzoWgEL62WGrV9UQy0DFlsU4dh73bAaOMxTBUxlbdrcJrhuUJwKRjdsljMLy2tkKkVbBdo2WxJWWqAZuQ=w798-h923-no",
        "https://lh3.googleusercontent.com/qVIQyBzvkn6UPtWMVzfD3zIxcNjQq6_xnw1-pAbo9qxbSe-if_7376eT2FWUJWoXVhVVXuTLIb3dRvSP4J37Z1LJmrd1TV0ItKwUbBJUzyuEs1Fx4Ec-qjWVCv5Sj4ELChLCOnGzSmbXMz_duQmgz-iQfLtD2NKWcyUBDijZOSp16owkxr04a6Uc4N-xSzRTq4C-xZi-iFvksdGjJuBFLvScjNkt3DCWQigUF9frzEzneB5tmjhD2YfSi9SPFg4HA5_ZDBL7jh5XXOLlcb6bfCebA55V1YX_IXMdcWMe5-TcEjMtHPAFaqx69jPwgLxiQ_yAYkK7G_lQ-fzIYq3oqOZLJQkxxfp7n--zqpYyrlLWfa-zRcrtIT1fH3KOczwDpnBX-DcyK6ygptoHW1JiRAxHFW3gD2aBBNjcSoJpM6y8xfNNrM3MPlRNeu_2Sl5cnZ-okSlJ0K9eEG3zyHQADk_LaGWUUmq5q1zkDSu1bhquynEu8hQ54kkNYn6GBtyisDRNMcV6MD-aZlmQgj8zVTIyfj7Rb_43D84RF1hYeVhdeR9T5veAH9ETHgcrt5t7ALi9wX4N1vfiVf1FdB2zfRpHArgJ7anzcv11GfixOQ=w693-h923-no"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_all_photos, null)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            var NUM_COLUMNS_PORTRAIT: Int = 3;
            initViews(NUM_COLUMNS_PORTRAIT) // 3 columns while straight
        } else {
            var NUM_COLUMNS_LANDSCAPE: Int = 4;
            initViews(NUM_COLUMNS_LANDSCAPE) // 4 columns while rotated
        }
    }

    private fun loadImages() {
        PicNoteDatabase.getDatabase(activity)?.let {
            it.picNoteDao().loadAll()
                    .take(10, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { picNotes ->
                        for (next in picNotes) {
                            Toast.makeText(activity, "${next.id} ${next.imageUri}", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun prepareData(): ArrayList<DisplayImage> {
        val android_version = ArrayList<DisplayImage>()
        for (i in 0 until android_version_names.size) {
            val androidVersion = DisplayImage(android_version_names[i], android_image_urls[i])
            android_version.add(androidVersion)
        }
        return android_version
    }

    private fun initViews(numColumn: Int) {
        // thing?.hi == if (thing != null) thing.hi else return null

        val recyclerView = getView()!!.findViewById<RecyclerView>(R.id.card_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(getActivity(), numColumn)
        recyclerView.layoutManager = layoutManager

        val androidVersions = prepareData()
        val adapter = DataAdapter(getActivity(), androidVersions)
        recyclerView.adapter = adapter
    }
}