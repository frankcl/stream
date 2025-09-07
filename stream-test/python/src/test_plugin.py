from sdk.py_plugin import PyPlugin
from sdk.py_plugin import ProcessResult

class Person:
    def __init__(self, name):
        self.name = name

class TestPlugin(PyPlugin):

    def handle(self, record):
        person = Person('frankcl')
        self.logger.info('create person: %s' % person.name)
        record.put('person', person)
        result = ProcessResult()
        result.addRecord('next', record)
        return result
