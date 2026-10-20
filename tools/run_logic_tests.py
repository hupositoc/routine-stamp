"""Run archived Java logic suites without claiming Android UI integration."""
from pathlib import Path
import subprocess
import tempfile

root=Path(__file__).resolve().parents[1]
with tempfile.TemporaryDirectory(prefix='routine-logic-') as directory:
    folder=Path(directory)
    resource=folder/'R.java'
    resource.write_text('package com.openai.routinequest; public final class R { public static final class drawable { public static final int stage_1=1,stage_2=2,stage_3=3,stage_4=4,stage_5=5; }}',encoding='utf-8')
    sources=[resource,root/'src/com/openai/routinequest/RoutineLogic.java',root/'RoutineLogicTestMain.java',root/'RoutineV3LogicTestMain.java']
    subprocess.run(['javac','-encoding','UTF-8','-d',str(folder)]+[str(p) for p in sources],check=True)
    for main in ['RoutineLogicTestMain','RoutineV3LogicTestMain']:
        subprocess.run(['java','-cp',str(folder),main],check=True)
