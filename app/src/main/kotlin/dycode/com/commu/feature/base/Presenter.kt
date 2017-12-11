package dycode.com.commu.feature.base

/**
 * Created by Asus on 9/19/2017.
 */
interface Presenter<V : MvpView> {
    fun attachView(mvpView: V)

    fun detachView()
}