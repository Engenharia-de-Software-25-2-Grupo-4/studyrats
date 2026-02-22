import { useState, useEffect, useRef } from "react"
import { authFetch } from "@/services/backendApi"

const blobToBase64 = (blob: Blob): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(blob)
  })
}

export function useImagemSessao(idSessao: string | null | undefined) {
  const [imagemBase64, setImagemBase64] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const pendenteRef = useRef(false)

  useEffect(() => {
    async function loadImagem() {
      if (!idSessao) {
        setImagemBase64(null)
        return
      }

      if (pendenteRef.current) return
      pendenteRef.current = true
      setLoading(true)

      try {
        const res = await authFetch(`/imagens/sessaoDeEstudo/${idSessao}`, {
          method: "GET",
        })

        if (!res.ok) {
          console.log(`Falha ao buscar imagem da sessão ${idSessao}:`, res.status)
          setImagemBase64(null)
          return
        }

        const blob = await res.blob()
        const base64 = await blobToBase64(blob)
        setImagemBase64(base64)
      } catch (error) {
        console.log(`Erro ao buscar imagem da sessão ${idSessao}:`, error)
        setImagemBase64(null)
      } finally {
        setLoading(false)
        pendenteRef.current = false
      }
    }

    loadImagem()
  }, [idSessao])

  return { imagemBase64, loading }
}