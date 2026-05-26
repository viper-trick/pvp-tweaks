import net.minecraft.client.option.KeyBinding;
public class test {
    public static void main(String[] args) {
        for (Class<?> c : KeyBinding.class.getDeclaredClasses()) {
            System.out.println(c.getName());
        }
    }
}
