import { useI18n } from 'vue-i18n'
import type { IJsonItem } from '../types'

export function useFlinkSqlGateway(model: { [field: string]: any }): IJsonItem[] {
  const { t } = useI18n()

  // === 加上绑定动作（关键！）===
  if (!model.flinkJdbcUrl) model.flinkJdbcUrl = 'jdbc:flink://host:port'
  if (!model.statementSeparator) model.statementSeparator = ';'
  if (!model.maxPrintRows) model.maxPrintRows = 0
  if (!model.initScript) model.initScript = ''
  if (!model.rawScript) model.rawScript = ''

  return [
      {
        type: 'input',
        field: 'flinkJdbcUrl',
        span: 24,
        name: 'JDBC URL',
        props: { placeholder: 'jdbc:flink://host:port' },
        value: model.flinkJdbcUrl,                    // ← 加上
        validate: { trigger: ['blur', 'input'], required: true }
      },
      {
        type: 'input',
        field: 'statementSeparator',
        span: 24,
        name: 'Statement Separator',
        props: { placeholder: ';' },
        value: model.statementSeparator          // ← 加上
      },
      {
        type: 'input-number',
        field: 'maxPrintRows',
        span: 24,
        name: 'Max Print Rows',
        props: { min: 0 },
        value: model.maxPrintRows                // ← 必须加上
      },
      {
        type: 'editor',
        field: 'initScript',
        span: 24,
        name: 'Init Script',
        props: { language: 'sql' },
        value: model.initScript                  // ← 加上更稳
      },
      {
        type: 'editor',
        field: 'rawScript',
        span: 24,
        name: 'Script',
        props: { language: 'sql' },
        value: model.rawScript,                  // ← 加上
        validate: { trigger: ['blur', 'input'], required: true }
      }
  ]
}
