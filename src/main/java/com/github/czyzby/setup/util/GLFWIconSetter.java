package com.github.czyzby.setup.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.kotcrab.vis.ui.util.OsUtils;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Atom;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeX11;

/**
 * @author Kotcrab
 */
public abstract class GLFWIconSetter {
    /**
     * Sets icon for application. Icon file must be square, typically 128x128. Larger files may not be supported
     * by all operating systems.
     * @throws IllegalStateException when error occurred during icon setting.
     */
    public abstract void setIcon(FileHandle icoFile, FileHandle pngFile) throws IllegalStateException;

    /**
     * @return new instance of {@link GLFWIconSetter} implementation for current platform or {@link DefaultGLFWIconSetter} when
     * no other implementation is available
     */
    public static GLFWIconSetter newInstance() {
        if (OsUtils.isWindows())
            return new WinGLFWIconSetter();

        try {
            Native.loadLibrary("X11", X11.class);
            return new X11GLFWIconSetter();
        } catch (UnsatisfiedLinkError e) {
            //X11 not present, ignoring
        }

        return new DefaultGLFWIconSetter();
    }

    /** Icon setter implementation for Windows */
    private static class WinGLFWIconSetter extends GLFWIconSetter {
        @Override
        public void setIcon(FileHandle icoFile, FileHandle pngFile) {
            //WinAPI can't read icon from JAR, needs copying to some other location
            FileHandle tmpIco = FileHandle.tempFile("gdx-setup");
            icoFile.copyTo(tmpIco);

            try {
                HANDLE hImage = User32.INSTANCE
                        .LoadImage(Kernel32.INSTANCE.GetModuleHandle(""), tmpIco.path(), WinUser.IMAGE_ICON, 0, 0,
                                WinUser.LR_LOADFROMFILE);
                User32.INSTANCE
                        .SendMessageW(User32.INSTANCE.GetActiveWindow(), User32.WM_SETICON, new WPARAM(User32.BIG_ICON),
                                hImage);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        public interface User32 extends StdCallLibrary, WinDef {
            User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

            int WM_SETICON = 0x0080;
            int BIG_ICON = 1;

            HWND GetActiveWindow();

            HANDLE LoadImage(HINSTANCE hInst, String name, int type, int xDesired, int yDesired, int load);

            LPARAM SendMessageW(HWND hWnd, int msg, WPARAM wParam, HANDLE lParam);
        }
    }

    /** Icon setter implementation for X11 */
    private static class X11GLFWIconSetter extends GLFWIconSetter {
        private static final String _NET_WM_ICON = "_NET_WM_ICON";

        private final X11 x11;

        public X11GLFWIconSetter() {
            x11 = X11.INSTANCE;
        }

        @Override
        public void setIcon(FileHandle icoFile, FileHandle pngFile) {
            Display display = null;
            try {
                display = x11.XOpenDisplay(null);

                Pixmap pixmap = new Pixmap(pngFile);
                if (pixmap.getWidth() != pixmap.getHeight()) {
                    throw new IllegalStateException("Supplied icon image must be square");
                }

                long buffer[] = new long[2 + pixmap.getWidth() * pixmap.getHeight()];
                buffer[0] = pixmap.getWidth();
                buffer[1] = pixmap.getHeight();

                int bufIndex = 2;
                for (int i = 0; i < pixmap.getWidth(); i++) {
                    for (int ii = 0; ii < pixmap.getHeight(); ii++) {
                        int color = pixmap.getPixel(ii, i);

                        //assuming pixmap is RGBA8888, buffer for x11 is ARGB8888
                        //probably not very optimized
                        int r = ((color & 0xff000000) >>> 24);
                        int g = ((color & 0x00ff0000) >>> 16);
                        int b = ((color & 0x0000ff00) >>> 8);
                        int a = ((color & 0x000000ff));

                        buffer[bufIndex++] = ((a << 24) | (r << 16) | (g << 8) | (b));
                    }
                }

                Window window = new Window(GLFWNativeX11.glfwGetX11Window(GLFW.glfwGetCurrentContext()));

                Pointer ptr = new Memory(buffer.length * 8);
                ptr.write(0, buffer, 0, buffer.length);
                x11.XChangeProperty(display, window, getAtom(display, _NET_WM_ICON), X11.XA_CARDINAL, 32,
                        X11.PropModeReplace, ptr, buffer.length);

                pixmap.dispose();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                if (display != null) {
                    x11.XCloseDisplay(display);
                }
            }
        }

        private Atom getAtom(Display display, String atom_name) {
            return x11.XInternAtom(display, atom_name, false);
        }
    }

    /** Fallback implementation for unsupported platforms, does nothing. */
    private static class DefaultGLFWIconSetter extends GLFWIconSetter {
        @Override
        public void setIcon(FileHandle icoFile, FileHandle pngFile) {

        }
    }
}