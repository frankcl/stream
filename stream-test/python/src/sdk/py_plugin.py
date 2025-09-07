import java
import logging.config
import os

KVRecord = java.type('xin.manong.weapon.base.record.KVRecord')
KVRecords = java.type('xin.manong.weapon.base.record.KVRecords')
ProcessResult = java.type("xin.manong.stream.sdk.common.ProcessResult")

logging.config.fileConfig('%s/logging.conf' % os.path.dirname(os.path.abspath(__file__)))

class PyPlugin:

    def __init__(self, configMap):
        self.configMap = configMap
        self.logger = logging.getLogger(__name__)

    def init(self):
        return True

    def destroy(self):
        pass

    def flush(self):
        pass

    def handle(self, record):
        pass