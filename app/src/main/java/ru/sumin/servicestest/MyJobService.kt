package ru.sumin.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*

//JobService с очередями
class MyJobService : JobService() {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent?.getIntExtra(PAGE_EXTRA, 0)
                    for (i in 0 until 5) {
                        delay(1000)
                        log("seconds: $i page: $page")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }

                jobFinished(params, true)
            }
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }


    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        coroutineScope.cancel()
    }

    private fun log(message: String) {
        Log.d("TEST_SERVICE", "Job Service: $message")
    }

    companion object {
        const val JOB_ID = 111
        const val PAGE_EXTRA = "page_extra"

        fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyJobService::class.java).apply {
                putExtra(PAGE_EXTRA, page)
            }
        }
    }
}