class Singleton{
  private static class singleton{
    private static final Singleton INSTANCE = new Singleton();
  }
  private Singleton getInstange(){
    return singleton.INSTANCE;
  }
}
