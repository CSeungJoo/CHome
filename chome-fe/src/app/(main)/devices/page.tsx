"use client";

import { Card } from "@/shared/ui";
import { useHubs } from "@/features/hub/hooks";
import Link from "next/link";
import { HiOutlineCube } from "react-icons/hi2";

export default function DevicesPage() {
  const { data, isLoading } = useHubs();

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-2">디바이스</h1>
      <p className="text-gray-500 text-sm mb-6">
        허브를 선택하면 해당 허브에 연결된 디바이스를 확인할 수 있습니다.
      </p>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin h-8 w-8 border-4 border-blue-600 border-t-transparent rounded-full" />
        </div>
      ) : data?.accessibleHubs.length === 0 ? (
        <Card>
          <div className="text-center py-8">
            <p className="text-gray-500">등록된 허브가 없습니다</p>
            <Link
              href="/hubs"
              className="text-blue-600 hover:underline text-sm mt-2 inline-block"
            >
              허브 등록하러 가기
            </Link>
          </div>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {data?.accessibleHubs.map((hub) => (
            <Link key={hub.id} href={`/hubs/${hub.id}`}>
              <Card className="hover:border-blue-300 hover:shadow-md transition-all cursor-pointer">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                    <HiOutlineCube className="w-5 h-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{hub.alias}</p>
                    <p className="text-xs text-gray-500">
                      S/N: {hub.serialNumber}
                    </p>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
