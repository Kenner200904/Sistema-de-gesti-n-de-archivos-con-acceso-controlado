#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$ROOT/out"
javac -encoding UTF-8 -d "$ROOT/out" "$ROOT"/src/*.java
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
SAMPLE="$ROOT/pruebas/archivo_ejemplo.txt"

printf '1\n%s\n3\n2\n1\n%s\n4\n' "$SAMPLE" "$TMP/descargas" \
  | (cd "$TMP" && java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "$ROOT/out" Cliente) \
  > "$TMP/salida_correcta.txt"
grep -Fq 'Archivo almacenado correctamente con ID 1.' "$TMP/salida_correcta.txt"
grep -Fq 'Se encontraron 1 archivo(s).' "$TMP/salida_correcta.txt"
grep -Fq 'Descarga completada.' "$TMP/salida_correcta.txt"
cmp "$SAMPLE" "$TMP/descargas/archivo_ejemplo.txt"

printf '1\n\n2\n999\n%s\n4\n' "$TMP/descargas" \
  | (cd "$TMP" && java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "$ROOT/out" Cliente) \
  > "$TMP/salida_validaciones.txt"
grep -Fq 'La ruta del archivo no puede estar vacía.' "$TMP/salida_validaciones.txt"
grep -Fq 'No existe un archivo con identificador 999.' "$TMP/salida_validaciones.txt"

printf 'PRUEBAS CORRECTAS\n'
printf ' - Subida y listado de archivos: OK\n'
printf ' - Descarga y comparación binaria con el original: OK\n'
printf ' - Validación de ruta vacía e ID inexistente: OK\n'
