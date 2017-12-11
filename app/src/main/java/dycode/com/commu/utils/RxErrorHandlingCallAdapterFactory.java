package dycode.com.commu.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * RxJava2 and Retrofit 2.2.0 compatible factory,
 * which wraps the {@link RxJavaCallAdapterFactory} and takes care of the error conversion.
 *
 * Based on: https://github.com/square/retrofit/issues/1102#issuecomment-241250796
 */
public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final RxJavaCallAdapterFactory mOriginalCallAdapterFactory;

    private RxErrorHandlingCallAdapterFactory() {
        mOriginalCallAdapterFactory = RxJavaCallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
        return new RxCallAdapterWrapper<>(retrofit, mOriginalCallAdapterFactory.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Observable<R>> {
        private final Retrofit mRetrofit;
        private final CallAdapter<R, ?> mWrappedCallAdapter;

        public RxCallAdapterWrapper(final Retrofit retrofit, final CallAdapter<R, ?> wrapped) {
            mRetrofit = retrofit;
            mWrappedCallAdapter = wrapped;
        }

        @Override
        public Type responseType() {
            return mWrappedCallAdapter.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Observable<R> adapt(final Call<R> call) {
            return ((Observable) mWrappedCallAdapter.adapt(call)).onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return Observable.error(asRetrofitExceptionUtil(throwable));
                }
            });
        }

        private RetrofitExceptionUtil asRetrofitExceptionUtil(final Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                final HttpException httpException = (HttpException) throwable;
                final Response response = httpException.response();

                return RetrofitExceptionUtil.httpError(response.raw().request().url().toString(), response, mRetrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitExceptionUtil.networkError((IOException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return RetrofitExceptionUtil.unexpectedError(throwable);
        }
    }
}