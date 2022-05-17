class FakeOutput implements Output {

    @Override
    public void println(String string) {
        System.out.println(string);
    }
}