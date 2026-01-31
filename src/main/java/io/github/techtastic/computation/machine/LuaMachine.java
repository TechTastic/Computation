package io.github.techtastic.computation.machine;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseStringLib;

public class LuaMachine extends BaseMachine {
    static Globals SERVER_GLOBALS;

    @Override
    public String getType() {
        return "lua";
    }

    @Override
    void init() {
        SERVER_GLOBALS = new Globals();
        SERVER_GLOBALS.load(new JseBaseLib());
        SERVER_GLOBALS.load(new PackageLib());
        SERVER_GLOBALS.load(new JseStringLib());
        SERVER_GLOBALS.load(new JseMathLib());

        LoadState.install(SERVER_GLOBALS);
        LuaC.install(SERVER_GLOBALS);

        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
    }

    @Override
    public Object[] runScript(String script) {
        Globals userGlobals = new Globals();
        userGlobals.load(new JseBaseLib());
        userGlobals.load(new PackageLib());
        userGlobals.load(new Bit32Lib());
        userGlobals.load(new TableLib());
        userGlobals.load(new JseStringLib());
        userGlobals.load(new JseMathLib());

        userGlobals.load(new DebugLib());
        LuaValue sethook = userGlobals.get("debug").get("sethook");
        userGlobals.set("debug", LuaValue.NIL);

        LuaValue chunk = SERVER_GLOBALS.load(script, "main", userGlobals);
        LuaThread thread = new LuaThread(userGlobals, chunk);

        LuaValue hookfunc = new ZeroArgFunction() {
            public LuaValue call() {
                throw new Error("Script overran resource limits.");
            }
        };
        final int instruction_count = 100;
        sethook.invoke(LuaValue.varargsOf(new LuaValue[] { thread, hookfunc,
                LuaValue.EMPTYSTRING, LuaValue.valueOf(instruction_count) }));

        Varargs result = thread.resume(LuaValue.NIL);
        Object[] arr = new Object[result.narg()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = result.arg(i).toString();
        }
        System.out.println("[["+script+"]] -> "+result);
        return arr;
    }

    static class ReadOnlyLuaTable extends LuaTable {
        public ReadOnlyLuaTable(LuaValue table) {
            presize(table.length(), 0);
            for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                    .next(n.arg1())) {
                LuaValue key = n.arg1();
                LuaValue value = n.arg(2);
                super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
            }
        }
        public LuaValue setmetatable(LuaValue metatable) { return error("table is read-only"); }
        public void set(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(LuaValue key, LuaValue value) { error("table is read-only"); }
        public LuaValue remove(int pos) { return error("table is read-only"); }
    }
}
