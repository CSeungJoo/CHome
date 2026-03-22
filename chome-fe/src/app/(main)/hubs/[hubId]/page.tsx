"use client";

import { use, useState, useEffect, useCallback, useRef } from "react";
import { Button, Card } from "@/shared/ui";
import { useDevices } from "@/features/device/hooks";
import { useSendHubCommand } from "@/features/hub/hooks";
import { connectHubSSE } from "@/features/hub/api";
import { useAuthStore } from "@/stores/auth-store";
import Link from "next/link";
import {
  HiOutlineCpuChip,
  HiOutlineSignal,
  HiOutlinePlay,
} from "react-icons/hi2";

export default function HubDetailPage({
  params,
}: {
  params: Promise<{ hubId: string }>;
}) {
  const { hubId: hubIdStr } = use(params);
  const hubId = Number(hubIdStr);
  const accessToken = useAuthStore((s) => s.accessToken);

  const { data: devicesData, isLoading: devicesLoading } = useDevices(hubId);
  const commandMutation = useSendHubCommand(hubId);

  const [sseConnected, setSseConnected] = useState(false);
  const [sseMessages, setSseMessages] = useState<string[]>([]);
  const eventSourceRef = useRef<EventSource | null>(null);

  const connectSSE = useCallback(() => {
    if (!accessToken || eventSourceRef.current) return;
    const es = connectHubSSE(hubId, accessToken);
    eventSourceRef.current = es;

    es.onopen = () => setSseConnected(true);
    es.onmessage = (event) => {
      setSseMessages((prev) => [...prev.slice(-49), event.data]);
    };
    es.onerror = () => {
      setSseConnected(false);
      es.close();
      eventSourceRef.current = null;
    };
  }, [hubId, accessToken]);

  useEffect(() => {
    return () => {
      eventSourceRef.current?.close();
    };
  }, []);

  const handleBleScan = () => {
    commandMutation.mutate({ type: "BLE_SCAN", payload: {} });
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">허브 #{hubId}</h1>
        <div className="flex gap-2">
          <Button
            variant={sseConnected ? "secondary" : "primary"}
            size="sm"
            onClick={connectSSE}
            disabled={sseConnected}
          >
            <HiOutlineSignal className="w-4 h-4 mr-1" />
            {sseConnected ? "SSE 연결됨" : "SSE 연결"}
          </Button>
          <Button
            size="sm"
            onClick={handleBleScan}
            loading={commandMutation.isPending}
          >
            <HiOutlinePlay className="w-4 h-4 mr-1" />
            BLE 스캔
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">디바이스 목록</h2>

          {devicesLoading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin h-6 w-6 border-4 border-blue-600 border-t-transparent rounded-full" />
            </div>
          ) : devicesData?.devices.length === 0 ? (
            <Card>
              <p className="text-center text-gray-500 py-4">
                연결된 디바이스가 없습니다
              </p>
            </Card>
          ) : (
            <div className="space-y-3">
              {devicesData?.devices.map((device) => (
                <Link key={device.id} href={`/devices/${device.id}`}>
                  <Card className="hover:border-blue-300 hover:shadow-md transition-all cursor-pointer">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                        <HiOutlineCpuChip className="w-5 h-5 text-green-600" />
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {device.alias || device.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          {device.type} | S/N: {device.serialNumber}
                        </p>
                      </div>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          )}
        </div>

        <div>
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            SSE 실시간 로그
          </h2>
          <Card className="h-80 overflow-y-auto">
            {sseMessages.length === 0 ? (
              <p className="text-center text-gray-400 py-8">
                {sseConnected
                  ? "메시지 대기 중..."
                  : "SSE에 연결하면 실시간 로그가 표시됩니다"}
              </p>
            ) : (
              <div className="space-y-1 font-mono text-xs">
                {sseMessages.map((msg, i) => (
                  <div key={i} className="text-gray-700 border-b border-gray-100 py-1">
                    {msg}
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
}
