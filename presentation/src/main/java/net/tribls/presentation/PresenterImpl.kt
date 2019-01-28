package net.tribls.presentation

import android.os.Handler
import android.os.Looper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.tribls.core.api.*
import net.tribls.core.api_models.ReviewsData
import net.tribls.models.MultiMediaModel
import net.tribls.models.ReviewModel
import net.tribls.models.ReviewsList
import java.io.File
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import java.net.UnknownHostException

// Decouple network check from the core layer to not leak dependencies by passing down the network check from the view
interface NetworkStateChecker {
    fun isOnline(): Boolean
}

class PresenterImpl(checker: NetworkStateChecker, file: File) : ReviewsPresenter {
    private val _checker = SoftReference(checker)
    private var viewRef : Reference<ReviewsView> = WeakReference(null)
    private val presentedView : ReviewsView?
        get() = viewRef.get()
    private val api = Api(object: NetworkChecker {
        override fun isOnline(): Boolean = _checker.get()?.isOnline() ?: false
    }, file)
    private var compositeDisposable = CompositeDisposable()

    override fun getReviewsForType(type: String, offset: Int, order: String) {
        /**
         * Add the Disposable.
         * observeOn() - When we have results, handle them on the main thread.
         * subscribeOn() - Perform the network operations in the IO thread.
         * Finally, in clearView(), clear the disposable to prevent a memory leak
         */
        compositeDisposable.add(api.rxGetReviews(type, offset, order)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ reviewsData ->
                    parseResponse(reviewsData, offset)
                }, { throwable ->
                    handleErrors(throwable)
                })
        )
    }

    private fun parseResponse(response: ReviewsData, lastOffset: Int)  {
        val reviews = response.results.map {review ->
            // Map the data to the model
            ReviewModel(
                review.display_title,
                review.mpaa_rating,
                review.byline,
                review.headline,
                review.summary_short,
                review.publication_date,
                MultiMediaModel(
                    review.multimedia.height,
                    review.multimedia.src,
                    review.multimedia.type,
                    review.multimedia.width
                )
            )
        }.let { modelsList ->
            ReviewsList(
                // add 20 so when the screen rotates, we don't re-load the last set
                lastOffset + 20,
                modelsList
            )
        }

        // Tell the view that we have reviews
        onMainThread {
            onReviewsAvailable(reviews)
        }
    }

    private fun handleErrors(it: Throwable){
        when(it){
            is NoNetworkException -> {
                onMainThread {
                    onNetworkLost()
                }
            }
            is UnknownHostException -> {
                onMainThread {
                    onNetworkLost()
                }
            }
            is UnauthorizedRequestException -> {
                onMainThread {

                }
            }
            is TooManyRequestsException -> {
                onMainThread {
                }
            }
            is ServerResponseException -> {
                onMainThread {

                }
            }
            is NoCacheException -> {
                onMainThread {

                }
            }
        }
    }

    fun setView(view: ReviewsView) {
        viewRef = WeakReference(view)
    }

    fun clearView() {
        viewRef.clear()
    }

    /**
     * Will execute [action] on the main thread only if [presentedView] is not null
     * @param action: The block of code to execute if the [presentedView] is not null
     */
    private inline fun onMainThread(crossinline action: ReviewsView.() -> Unit) {
        val looper = Looper.getMainLooper()
        val handler =  Handler(looper)
        handler.post {
            presentedView?.let { view ->
                action.invoke(view)
            }
        }
    }
}