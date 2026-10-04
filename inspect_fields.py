import glob, os, subprocess

loom_jars = glob.glob(os.path.expanduser("~/.gradle/caches/fabric-loom/**/minecraft-*.jar"), recursive=True) + \
            glob.glob(os.path.expanduser("~/.gradle/caches/**/minecraft*.jar"), recursive=True)

for jar in loom_jars:
    if "26.3" in jar or "26_3" in jar or "1.21" in jar:
        res = subprocess.run(["javap", "-private", "-cp", jar, "net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer"], capture_output=True, text=True)
        if res.returncode == 0:
            print("=== FirstPersonHandsAndItemsRenderer Fields ===")
            for line in res.stdout.splitlines():
                if "ItemStack" in line or "Item" in line or "off" in line.lower() or "hand" in line.lower():
                    print(line)
            break
