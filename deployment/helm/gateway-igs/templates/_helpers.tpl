{{/*
Expand the name of the chart.
*/}}
{{- define "gateway-igs.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "gateway-igs.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "gateway-igs.fullversionname" -}}
{{- if .Values.istio.enable }}
{{- $name := include "gateway-igs.fullname" . }}
{{- $version := regexReplaceAll "\\.+" .Chart.Version "-" }}
{{- printf "%s-%s" $name $version | trunc 63 }}
{{- else }}
{{- include "gateway-igs.fullname" . }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "gateway-igs.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "gateway-igs.labels" -}}
helm.sh/chart: {{ include "gateway-igs.chart" . }}
{{ include "gateway-igs.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- if .Values.istio.enable }}
version: {{ .Chart.AppVersion | quote }}
{{- end }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- with .Values.customLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "gateway-igs.selectorLabels" -}}
{{- if .Values.istio.enable }}
app: {{ include "gateway-igs.name" . }}
version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/name: {{ include "gateway-igs.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Deployment labels
*/}}
{{- define "gateway-igs.deploymentLabels" -}}
{{ if .Values.istio.enable -}}
istio-validate-jwt: "{{ .Values.istio.validateJwt | required ".Values.istio.validateJwt is required" }}"
{{- with .Values.deploymentLabels }}
{{ toYaml . }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "gateway-igs.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "gateway-igs.fullversionname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
