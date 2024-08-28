package bd.com.momagic.cbs.dashboard.android.core.dependencyinjection;

public interface ServiceInstantiator<Type> {
    Type instantiate();
}
