<template>
    <div class="medical-records">
        <h2 class="page-title">鐥呭巻绠＄悊</h2>
        <div class="search-section">

            <el-input v-model="searchText" placeholder="璇疯緭鍏ョ梾鍘咺D" class="search-input">
            </el-input>
            <el-button type="primary" @click="handleSearch" class="search-button">鎼滅储鐥呭巻</el-button>
            <el-button type="primary" @click="openFillInDialog" class="add-button">濉啓鐥呭巻</el-button>
        </div>

        <el-table :data="tableData" border style="min-width: 100%">
            <el-table-column fixed prop="recordId" label="鐥呭巻ID" width="120" />
            <el-table-column prop="patientName" label="鎮ｈ€呭鍚? width="120" />
            <el-table-column prop="hospitalName" label="鍖婚櫌鍚嶇О" width="180" />
            <el-table-column prop="department" label="绉戝" width="120" />
            <el-table-column prop="doctorName" label="鍖荤敓濮撳悕" width="120" />
            <el-table-column prop="createTime" label="鍒涘缓鏃堕棿" width="120" />
            <el-table-column prop="isFilled" label="鐥呭巻鐘舵€? width="100">
                <template #default="scope">
                    <el-tag :type="scope.row.isFilled ? 'success' : 'info'">
                        {{ scope.row.isFilled ? '宸插畬鎴? : '鏈畬鎴? }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="鎿嶄綔">
                <template #default="scope">
                    <el-button type="primary" @click="completeRecord(scope.row)">瀹屾垚</el-button>
                    <el-button type="info" @click="EditRecord(scope.row)">缂栬緫</el-button>
                    <el-button type="danger" @click="deleteRecord(scope.row)">鍒犻櫎</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]" :total="total" layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%" @close="closeDialog">
            <el-form :model="recordForm" :rules="rules" ref="recordFormRef">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="鐥呭巻缂栧彿" v-if="dialogTitle !== '濉啓鐥呭巻'">
                        <el-input v-model="recordForm.recordId" :disabled="true" />
                    </el-descriptions-item>
                    <el-descriptions-item label="鎮ｈ€呭鍚?>
                        <el-autocomplete
                            v-model="recordForm.patientName"
                            :fetch-suggestions="queryPatientNames"
                            placeholder="璇疯緭鍏ユ偅鑰呭鍚?
                            :trigger-on-focus="true"
                            clearable
                            :disabled="viewOnly"
                            @select="handlePatientSelect"
                            @focus="onPatientInputFocus"
                        ></el-autocomplete>
                    </el-descriptions-item>

                    <el-descriptions-item label="鍖婚櫌鍚嶇О">
                        <el-autocomplete
                            v-model="recordForm.hospitalName"
                            :fetch-suggestions="queryHospitals"
                            placeholder="璇疯緭鍏ユ垨閫夋嫨鍖婚櫌"
                            clearable
                            :disabled="viewOnly"
                            @select="handleHospitalSelect"
                        ></el-autocomplete>
                    </el-descriptions-item>
                    <el-descriptions-item label="绉戝">
                        <el-autocomplete
                            v-model="recordForm.department"
                            :fetch-suggestions="queryDepartments"
                            placeholder="璇疯緭鍏ユ垨閫夋嫨绉戝"
                            clearable
                            :disabled="viewOnly"
                            @select="handleDepartmentSelect"
                        ></el-autocomplete>
                    </el-descriptions-item>
                    <el-descriptions-item label="鍖荤敓濮撳悕">
                        <el-input v-model="recordForm.doctorName" :disabled="viewOnly" />
                    </el-descriptions-item>

                    <el-descriptions-item label="鎸傚彿淇℃伅">
                        <el-input v-model="recordForm.registrationInfo" type="textarea" :rows="2"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="鏃㈠線鐥呭彶">
                        <el-input v-model="recordForm.pastMedicalHistory" type="textarea" :rows="3"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="鐜扮梾鍙?>
                        <el-input v-model="recordForm.currentMedicalHistory" type="textarea" :rows="3"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="鐘舵€?>{{ recordForm.isFilled ? '宸插畬鎴? : '鏈畬鎴?
                    }}</el-descriptions-item>
                    <el-descriptions-item label="鍒涘缓鏃ユ湡">
                        <template v-if="dialogTitle === '濉啓鐥呭巻'">
                            <span>{{ currentDateTime }}</span>
                        </template>
                        <template v-else>
                            {{ formatDateTime(recordForm.createdAt) }}
                        </template>
                    </el-descriptions-item>
                </el-descriptions>

            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">鍙栨秷</el-button>
                    <el-button type="primary" @click="saveRecord">淇濆瓨</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- <el-dialog v-model="infoDialogVisible" title="鐥呭巻璇︽儏" width="60%">
            <div>
                <p><strong>鐥呭巻ID:</strong> {{ medicalRecordDetails.recordId }}</p>
                <p><strong>鎮ｈ€呭鍚?</strong> {{ medicalRecordDetails.patientName }}</p>
                <p><strong>鍖婚櫌鍚嶇О:</strong> {{ medicalRecordDetails.hospitalName }}</p>
                <p><strong>绉戝:</strong> {{ medicalRecordDetails.department }}</p>
                <p><strong>鍖荤敓濮撳悕:</strong> {{ medicalRecordDetails.doctorName }}</p>
                <p><strong>鎸傚彿淇℃伅:</strong> {{ medicalRecordDetails.registrationInfo }}</p>
                <p><strong>鏃㈠線鐥呭彶:</strong> {{ medicalRecordDetails.pastMedicalHistory }}</p>
                <p><strong>鐜扮梾鍙?</strong> {{ medicalRecordDetails.currentMedicalHistory }}</p>
            </div>
            <template #footer>
                <el-button @click="infoDialogVisible = false">鍏抽棴</el-button>
            </template>
        </el-dialog> -->
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus';
import {getMedicalRecordByIdentityNumber, getMedicalRecordList, completeMedicalRecord, createMedicalRecord, updateMedicalRecord, deleteMedicalRecord, getAllPatientsName } from "../../api/medical";

const searchText = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const viewOnly = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
const loading = ref(false)

// Define recordForm as a reactive object
const recordForm = reactive({
    recordId: 0,
    patientName: '',
    doctorName: '',
    hospitalName: '',
    department: '',
    registrationInfo: '',
    pastMedicalHistory: '',
    currentMedicalHistory: '',
    isFilled: false,
    createdAt: 0
});

const recordFormRef = ref<FormInstance>();
const userId = parseInt(localStorage.getItem("userId") || "0");

// 娣诲姞褰撳墠娴佸姩鐨勬椂闂?const currentDateTime = ref('');
let timeInterval: ReturnType<typeof setInterval> | null = null;

// 鏇存柊褰撳墠鏃堕棿鐨勫嚱鏁?const updateCurrentTime = () => {
    currentDateTime.value = formatDateTime(Date.now());
};

// 瀛樺偍鎵€鏈夋偅鑰呬俊鎭?const patientsList = ref<Array<{patientId: number, patientName: string}>>([]);

// 鍦ㄨ緭鍏ユ鑾峰緱鐒︾偣鏃朵篃灏濊瘯鍔犺浇鏁版嵁
const onPatientInputFocus = () => {
    console.log("鎮ｈ€呰緭鍏ユ鑾峰緱鐒︾偣");
    if (patientsList.value.length === 0) {
        console.log("灏濊瘯鍔犺浇鎮ｈ€呭垪琛?..");
        fetchPatients();
    }
};

// 鑾峰彇鎵€鏈夋偅鑰呬俊鎭?const fetchPatients = async () => {
    console.log("寮€濮嬭幏鍙栨偅鑰呭垪琛?..");
    
    // 娣诲姞涓€浜涙祴璇曟暟鎹紝浠ラ槻API鏆傛椂涓嶅伐浣?    const testPatients = [
        { patientId: 1, patientName: "寮犱笁" },
        { patientId: 2, patientName: "鏉庡洓" },
        { patientId: 3, patientName: "鐜嬩簲" }
    ];
    
    try {
        const response = await getAllPatientsName();
        console.log("API杩斿洖鍘熷鎮ｈ€呮暟鎹?", response);
        
        if (response && Array.isArray(response) && response.length > 0) {
            patientsList.value = response.map((patient: any) => ({
                patientId: patient.patientId,
                patientName: patient.patientName,
            }));
            console.log('鑾峰彇鎮ｈ€呭垪琛ㄦ垚鍔燂紝鏁伴噺:', patientsList.value.length);
            return Promise.resolve(response);
        } else {
            console.warn('API鏈繑鍥炴湁鏁堟偅鑰呮暟鎹紝浣跨敤娴嬭瘯鏁版嵁');
            patientsList.value = testPatients;
            return Promise.resolve(testPatients);
        }
    } catch (error) {
        console.error('鑾峰彇鎮ｈ€呭垪琛ㄥけ璐?, error);
        console.warn('浣跨敤娴嬭瘯鏁版嵁浠ｆ浛');
        patientsList.value = testPatients;
        return Promise.resolve(testPatients);
    }
};

// 鏌ヨ杩囨护鎮ｈ€呭鍚?const queryPatientNames = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("鏌ヨ鎮ｈ€咃紝鏌ヨ瀛楃涓?", queryString, "褰撳墠鍒楄〃闀垮害:", patientsList.value.length);
    
    // 濡傛灉鍒楄〃涓虹┖鎴栭暱搴︿负0锛屼娇鐢ㄦ祴璇曟暟鎹?    if (!patientsList.value || patientsList.value.length === 0) {
        console.log("鎮ｈ€呭垪琛ㄤ负绌猴紝灏濊瘯鍔犺浇...");
        fetchPatients().then(() => {
            console.log("鍔犺浇鍚庤繃婊ゆ偅鑰?);
            filterPatients(queryString, callback);
        });
    } else {
        console.log("浣跨敤鐜版湁鎮ｈ€呭垪琛ㄨ繃婊?);
        filterPatients(queryString, callback);
    }
};

// 杩囨护鎮ｈ€呮暟鎹?const filterPatients = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("杩囨护鎮ｈ€咃紝杈撳叆瀛楃:", queryString);
    
    // 纭繚鏈夎嚦灏戜竴涓偅鑰呮暟鎹?    if (!patientsList.value || patientsList.value.length === 0) {
        // 濡傛灉娌℃湁鏁版嵁锛岃繑鍥炴祴璇曟暟鎹?        const testPatients = [
            { patientId: 1, patientName: "寮犱笁" },
            { patientId: 2, patientName: "鏉庡洓" },
            { patientId: 3, patientName: "鐜嬩簲" }
        ].map(patient => ({
            value: patient.patientName,
            id: String(patient.patientId),
            label: `${patient.patientName}`,
        }));
        console.log("娌℃湁鎮ｈ€呮暟鎹紝浣跨敤娴嬭瘯鏁版嵁");
        callback(testPatients);
        return;
    }
    
    // 濡傛灉鏌ヨ瀛楃涓蹭负绌烘垨鏈畾涔夛紝杩斿洖鎵€鏈夋偅鑰?    if (!queryString) {
        const allPatients = patientsList.value.map(patient => ({
            value: patient.patientName,
            id: String(patient.patientId || 0),
            label: `${patient.patientName}`,
        }));
        console.log("杩斿洖鎵€鏈夋偅鑰?", allPatients.length);
        callback(allPatients);
        return;
    }
    
    // 瀵规偅鑰呭悕杩涜杩囨护锛屾敮鎸佸崟涓瓧绗﹀尮閰?    const results = patientsList.value.filter(patient => {
        if (!patient || !patient.patientName) return false;
        // 瀵规偅鑰呭悕杩涜瀛楃鍖归厤锛屽拷鐣ュぇ灏忓啓
        return patient.patientName.toLowerCase().includes(queryString.toLowerCase());
    });
    
    console.log("杩囨护鍚庣殑缁撴灉鏁伴噺:", results.length);
    
    // 杞崲涓篴utocomplete鎵€闇€鏍煎紡
    const suggestions = results.map(patient => ({
        value: patient.patientName,
        id: String(patient.patientId || 0),
        label: `${patient.patientName}`
    }));
    
    console.log("杩斿洖鐨勬偅鑰呭缓璁垪琛ㄦ暟閲?", suggestions.length);
    callback(suggestions);
};

// 閫夋嫨鎮ｈ€呭鐞?const handlePatientSelect = (item: any) => {
    recordForm.patientName = item.value;
    console.log('宸查€夋嫨鎮ｈ€?', item);
};

// 绉戝閫夐」鍒楄〃
const departmentOptions = [
    { value: '鍐呯', label: '鍐呯' },
    { value: '澶栫', label: '澶栫' },
    { value: '濡囦骇绉?, label: '濡囦骇绉? },
    { value: '鍎跨', label: '鍎跨' },
    { value: '鐪肩', label: '鐪肩' },
    { value: '鍙ｈ厰绉?, label: '鍙ｈ厰绉? },
    { value: '鑰抽蓟鍠夌', label: '鑰抽蓟鍠夌' },
    { value: '鐨偆绉?, label: '鐨偆绉? },
    { value: '绁炵粡绉?, label: '绁炵粡绉? },
    { value: '绮剧蹇冪悊绉?, label: '绮剧蹇冪悊绉? },
    { value: '鑲跨槫绉?, label: '鑲跨槫绉? },
    { value: '涓尰绉?, label: '涓尰绉? },
    { value: '鎬ヨ瘖绉?, label: '鎬ヨ瘖绉? },
    { value: '妫€楠岀', label: '妫€楠岀' },
    { value: '鏀惧皠绉?, label: '鏀惧皠绉? },
    { value: '楹婚唹绉?, label: '楹婚唹绉? },
    { value: '鏁村舰绉?, label: '鏁村舰绉? },
    { value: '钀ュ吇绉?, label: '钀ュ吇绉? },
    { value: '搴峰绉?, label: '搴峰绉? }
];

// 鏌ヨ杩囨护绉戝
const queryDepartments = (queryString: string, callback: (suggestions: any[]) => void) => {
    const results = queryString
        ? departmentOptions.filter(
            (dept) => dept.value.toLowerCase().includes(queryString.toLowerCase())
        )
        : departmentOptions;
    
    callback(results);
};

// 閫夋嫨绉戝澶勭悊
const handleDepartmentSelect = (item: any) => {
    recordForm.department = item.value;
};

// 甯歌鍖婚櫌鍒楄〃
const hospitalOptions = [
    { value: '鍖椾含鍗忓拰鍖婚櫌', label: '鍖椾含鍗忓拰鍖婚櫌' },
    { value: '棣栭兘鍖荤澶у闄勫睘鍖椾含澶╁潧鍖婚櫌', label: '棣栭兘鍖荤澶у闄勫睘鍖椾含澶╁潧鍖婚櫌' },
    { value: '鍖椾含澶у绗竴鍖婚櫌', label: '鍖椾含澶у绗竴鍖婚櫌' },
    { value: '鍖椾含澶у浜烘皯鍖婚櫌', label: '鍖椾含澶у浜烘皯鍖婚櫌' },
    { value: '鍖椾含澶у绗笁鍖婚櫌', label: '鍖椾含澶у绗笁鍖婚櫌' },
    { value: '涓浗鍖诲绉戝闄㈠寳浜崗鍜屽尰闄?, label: '涓浗鍖诲绉戝闄㈠寳浜崗鍜屽尰闄? },
    { value: '涓浗鍖诲绉戝闄㈣偪鐦ゅ尰闄?, label: '涓浗鍖诲绉戝闄㈣偪鐦ゅ尰闄? },
    { value: '棣栭兘鍖荤澶у闄勫睘鍖椾含鍙嬭皧鍖婚櫌', label: '棣栭兘鍖荤澶у闄勫睘鍖椾含鍙嬭皧鍖婚櫌' },
    { value: '棣栭兘鍖荤澶у瀹ｆ鍖婚櫌', label: '棣栭兘鍖荤澶у瀹ｆ鍖婚櫌' },
    { value: '鍖椾含涓尰鑽ぇ瀛︿笢鐩撮棬鍖婚櫌', label: '鍖椾含涓尰鑽ぇ瀛︿笢鐩撮棬鍖婚櫌' },
    { value: '澶嶆棪澶у闄勫睘涓北鍖婚櫌', label: '澶嶆棪澶у闄勫睘涓北鍖婚櫌' },
    { value: '澶嶆棪澶у闄勫睘鍗庡北鍖婚櫌', label: '澶嶆棪澶у闄勫睘鍗庡北鍖婚櫌' },
    { value: '涓婃捣浜ら€氬ぇ瀛﹀尰瀛﹂櫌闄勫睘鐟為噾鍖婚櫌', label: '涓婃捣浜ら€氬ぇ瀛﹀尰瀛﹂櫌闄勫睘鐟為噾鍖婚櫌' },
    { value: '涓婃捣浜ら€氬ぇ瀛﹀尰瀛﹂櫌闄勫睘浠佹祹鍖婚櫌', label: '涓婃捣浜ら€氬ぇ瀛﹀尰瀛﹂櫌闄勫睘浠佹祹鍖婚櫌' },
    { value: '涓北澶у闄勫睘绗竴鍖婚櫌', label: '涓北澶у闄勫睘绗竴鍖婚櫌' },
];

// 鏌ヨ杩囨护鍖婚櫌
const queryHospitals = (queryString: string, callback: (suggestions: any[]) => void) => {
    const results = queryString
        ? hospitalOptions.filter(
            (hospital) => hospital.value.toLowerCase().includes(queryString.toLowerCase())
        )
        : hospitalOptions;
    
    callback(results);
};

// 閫夋嫨鍖婚櫌澶勭悊
const handleHospitalSelect = (item: any) => {
    recordForm.hospitalName = item.value;
};

const handleSearch = async () => {
    if (searchText.value == '') {
        ElMessage.error('璇疯緭鍏ョ梾鍘咺D');
        return;
    }

    try {
        const recordId = parseInt(searchText.value);
        if (isNaN(recordId)) {
            ElMessage.error('鐥呭巻ID蹇呴』鏄暟瀛?);
            return;
        }
        
        // 涓存椂灏濊瘯浣跨敤鍏朵粬API鏂规硶鑾峰彇鐥呭巻璇︽儏
        loading.value = true;
        console.log(`灏濊瘯鎼滅储鐥呭巻锛孖D: ${recordId}, 鐢ㄦ埛ID: ${userId}`);
        
        // 棣栧厛灏濊瘯浣跨敤getMedicalRecord鑾峰彇鐥呭巻
        let res = await getMedicalRecordByIdentityNumber(recordId, userId);
        console.log('鐥呭巻鏌ヨ缁撴灉:', res); // 娣诲姞鏃ュ織鏌ョ湅鍝嶅簲缁撴瀯

        // 纭繚res涓嶄负绌轰笖鏈夋暟鎹?        if (res) {
            // 閲嶇疆琛ㄥ崟锛岀‘淇濇病鏈夋棫鏁版嵁
            resetRecordForm();
            
            // 鐩存帴璁剧疆灞炴€ц€屼笉鏄娇鐢∣bject.assign
            recordForm.recordId = res.recordId;
            recordForm.patientName = res.patientName || '';
            recordForm.doctorName = res.doctorName || '';
            recordForm.hospitalName = res.hospitalName || '';
            recordForm.department = res.department || '';
            recordForm.registrationInfo = res.registrationInfo || '';
            recordForm.pastMedicalHistory = res.pastMedicalHistory || '';
            recordForm.currentMedicalHistory = res.currentMedicalHistory || '';
            recordForm.isFilled = res.isFilled || false;
            recordForm.createdAt = res.createTime || Date.now();

            // 璁剧疆瑙嗗浘鐘舵€?            viewOnly.value = res.isFilled;
            dialogTitle.value = '鐥呭巻璇︽儏';
            
            // 纭繚瀵硅瘽妗嗗彲瑙?            dialogVisible.value = true;
            
            // 閫氱煡鐢ㄦ埛鏌ヨ鎴愬姛
            ElMessage.success('鏌ヨ鐥呭巻鎴愬姛');
        } else {
            ElMessage.error('鎼滅储澶辫触锛屾湭鎵惧埌鐥呭巻淇℃伅');
        }
    } catch (error) {
        console.error('鎼滅储鐥呭巻鍑洪敊:', error);
        ElMessage.error('鎼滅储澶辫触');
    } finally {
        loading.value = false;
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val;
    getMedicalRecord();
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    getMedicalRecord();
}

const completeRecord = (row: any) => {
    ElMessageBox.confirm('纭瀹屾垚姝ょ梾鍘?', '鎻愮ず', {
        confirmButtonText: '纭畾',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
    }).then(async () => {
        try {
            const recordId = parseInt(row.recordId);
            const response = await completeMedicalRecord(recordId);
            console.log('瀹屾垚鐥呭巻鍝嶅簲:', response); // 鏌ョ湅鍝嶅簲缁撴瀯

            if (response && response.code === 200) {
                ElMessage.success('鐥呭巻宸插畬鎴?);
                getMedicalRecord();
            } else {
                ElMessage.error(response?.msg || '鎿嶄綔澶辫触');
            }
        } catch (error) {
            ElMessage.error('鎿嶄綔澶辫触');
        }
    });
}

const openFillInDialog = () => {
    resetRecordForm();
    dialogTitle.value = '濉啓鐥呭巻';
    viewOnly.value = false;
    dialogVisible.value = true;

    // 鍚姩鏃堕棿娴侀€濇晥鏋?    updateCurrentTime(); // 绔嬪嵆鏇存柊涓€娆?    if (timeInterval) clearInterval(timeInterval);
    timeInterval = setInterval(updateCurrentTime, 1000); // 姣忕鏇存柊涓€娆?}

// 琛ㄥ崟楠岃瘉瑙勫垯
const rules = {

    patientName: [
        { required: true, message: '璇疯緭鍏ユ偅鑰呭鍚?, trigger: 'blur' }
    ],
    doctorName: [
        { required: true, message: '璇疯緭鍏ュ尰鐢熷鍚?, trigger: 'blur' }
    ],
    hospitalName: [
        { required: true, message: '璇疯緭鍏ュ尰闄㈠悕绉?, trigger: 'blur' }
    ],
    department: [
        { required: true, message: '璇疯緭鍏ョ瀹?, trigger: 'blur' }
    ]
};

const saveRecord = async () => {
    if (!recordFormRef.value) return;

    try {
        await recordFormRef.value.validate(async (valid) => {
            if (valid) {
                // 纭繚patientName鏄瓧绗︿覆绫诲瀷
                if (typeof recordForm.patientName !== 'string') {
                    recordForm.patientName = String(recordForm.patientName);
                }

                // 纭繚doctorName鏄瓧绗︿覆绫诲瀷
                if (typeof recordForm.doctorName !== 'string') {
                    recordForm.doctorName = String(recordForm.doctorName);
                }

                // 鑾峰彇褰撳墠鏃堕棿鎴筹紝鐢ㄤ簬鍙戦€佸埌鍚庣
                const currentTimestamp = Date.now();

                if (dialogTitle.value === "濉啓鐥呭巻") {
                    // 鍒涘缓鏂扮梾鍘嗭紝浣跨敤褰撳墠鏃堕棿鎴?                    recordForm.createdAt = currentTimestamp;
                    const response = await createMedicalRecord(
                        recordForm.patientName,
                        recordForm.doctorName,
                        recordForm.hospitalName,
                        recordForm.department,
                        recordForm.registrationInfo,
                        recordForm.pastMedicalHistory,
                        recordForm.currentMedicalHistory,
                        currentTimestamp // 鍙戦€佹椂闂存埑鍒板悗绔?                    );
                    console.log('鍒涘缓鐥呭巻鍝嶅簲:', response); // 鏌ョ湅鍝嶅簲缁撴瀯

                    if (response && response.code === 200) {
                        ElMessage.success('鐥呭巻濉啓鎴愬姛');
                        dialogVisible.value = false;
                        getMedicalRecord();
                        resetRecordForm();
                    } else {
                        ElMessage.error("鐥呭巻濉啓澶辫触");
                    }
                } else {
                    // 鏇存柊鐜版湁鐥呭巻锛屼娇鐢ㄥ綋鍓嶆椂闂存埑
                    recordForm.createdAt = currentTimestamp;
                    const response = await updateMedicalRecord(
                        recordForm.recordId,
                        recordForm.hospitalName,
                        recordForm.department,
                        recordForm.registrationInfo,
                        recordForm.pastMedicalHistory,
                        recordForm.currentMedicalHistory,
                        currentTimestamp // 鍙戦€佹椂闂存埑鍒板悗绔?                    );
                    console.log('鏇存柊鐥呭巻鍝嶅簲:', response); // 鏌ョ湅鍝嶅簲缁撴瀯

                    if (response && response.code === 200) {
                        ElMessage.success("鐥呭巻璇︽儏淇濆瓨鎴愬姛");
                        dialogVisible.value = false;
                        getMedicalRecord();
                        resetRecordForm();
                    } else {
                        ElMessage.error("鐥呭巻璇︽儏淇濆瓨澶辫触");
                    }
                }

                // 瀵硅瘽妗嗗叧闂椂娓呴櫎瀹氭椂鍣?                if (timeInterval) {
                    clearInterval(timeInterval);
                    timeInterval = null;
                }
            } else {
                ElMessage.warning('璇峰～鍐欏畬鏁寸梾鍘嗕俊鎭苟纭繚鏍煎紡姝ｇ‘');
            }
        });
    } catch (error) {
        ElMessage.error('濉啓鐥呭巻澶辫触');
    }
};

const EditRecord = (row: any) => {
    // 鍒涘缓鍓湰閬垮厤鐩存帴寮曠敤
    Object.assign(recordForm, {
        recordId: row.recordId,
        patientName: typeof row.patientName === 'number' ? String(row.patientName) : row.patientName,
        doctorName: typeof row.doctorName === 'number' ? String(row.doctorName) : row.doctorName,
        hospitalName: row.hospitalName,
        department: row.department,
        registrationInfo: row.registrationInfo,
        pastMedicalHistory: row.pastMedicalHistory,
        currentMedicalHistory: row.currentMedicalHistory,
        isFilled: row.isFilled,
        createdAt: row.createTime && typeof row.createTime === 'string' && !isNaN(Date.parse(row.createTime))
            ? Date.parse(row.createTime)
            : (row.createdAt || Date.now())
    });
    viewOnly.value = row.isFilled;
    dialogTitle.value = '鐥呭巻璇︽儏';
    dialogVisible.value = true;
}

const deleteRecord = async (row: any) => {
    ElMessageBox.confirm('纭鍒犻櫎姝ょ梾鍘?', '鎻愮ず', {
        confirmButtonText: '纭畾',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
    }).then(async () => {
        try {
            const recordId = parseInt(row.recordId);
            const response = await deleteMedicalRecord(recordId);

            if (response && response.code === 200) {
                ElMessage.success('鍒犻櫎鎴愬姛');
                getMedicalRecord();
            } else {
                ElMessage.error(response?.msg || '鍒犻櫎澶辫触');
            }
        } catch (error) {
            ElMessage.error('鍒犻櫎澶辫触');
        }
    });
}

const formatDateTime = (timestamp: number) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

const getMedicalRecord = async () => {
    try {
        loading.value = true;
        const res = await getMedicalRecordList(
            userId
        );
        console.log('鑾峰彇鐥呭巻鍒楄〃鍝嶅簲:', res);
        tableData.value = res.map((record: any) => ({
            ...record,
            createTime: formatDateTime(record.createTime)
           
        }));
    } catch (error) {
        console.error('鑾峰彇鐥呭巻鍒楄〃澶辫触:', error);
        ElMessage.error('鑾峰彇鐥呭巻鍒楄〃澶辫触');
    } finally {
        loading.value = false;
    }
}

const resetRecordForm = () => {
    // Reset form fields
    Object.assign(recordForm, {
        recordId: '',
        patientName: '',
        doctorName: localStorage.getItem("name") || '', // 榛樿涓哄綋鍓嶇櫥褰曞尰鐢?        hospitalName: '',
        department: '',
        registrationInfo: '',
        pastMedicalHistory: '',
        currentMedicalHistory: '',
        isFilled: false,
        createdAt: 0
    });
};

// 鍦ㄥ叧闂璇濇鏃舵竻闄ゅ畾鏃跺櫒
const closeDialog = () => {
    if (timeInterval) {
        clearInterval(timeInterval);
        timeInterval = null;
    }
    resetRecordForm();
}

onMounted(() => {
    getMedicalRecord();
    // fetchPatients(); // 涓嶅啀闇€瑕佸湪鎸傝浇鏃惰幏鍙栨偅鑰呭垪琛?    
    // 鍒濆鍖栭噸缃〃鍗?    resetRecordForm();
    
    // 璁剧疆鏇存柊鏃堕棿鐨勫畾鏃跺櫒
    updateCurrentTime(); 
});

// 鍦ㄧ粍浠跺嵏杞芥椂娓呴櫎瀹氭椂鍣?onUnmounted(() => {
    if (timeInterval) {
        clearInterval(timeInterval);
    }
});
</script>

<style scoped>
.medical-records {
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 15px;
    position: relative;
    overflow: hidden;
}

.medical-records::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
}

/* 鎼滅储鍖哄煙鏍峰紡 */
.search-section {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 12px;
    position: relative;
    border: 1px solid rgba(33, 150, 243, 0.1);
    box-shadow: 0 4px 20px rgba(149, 157, 165, 0.1);
}

/* 娣诲姞绉戞妧鎰熻楗扮嚎鏉?*/
/* .search-section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 20px;
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, #2196F3, transparent);
}

.search-section::after {
    content: '';
    position: absolute;
    bottom: 0;
    right: 20px;
    width: 60px;
    height: 3px;
    background: linear-gradient(360deg, transparent, #2196F3);
} */
.page-title {
    font-size: 24px;
    color: #2c3e50;
    margin-bottom: 25px;
    font-weight: 600;
    display: flex;
    align-items: center;
    position: relative;
    padding-left: 15px;
}

.page-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 24px;
    background: linear-gradient(180deg, #4a9e5c, #2196F3);
    border-radius: 2px;
}

.search-label {
    margin-right: 15px;
    font-size: 15px;
    font-weight: 500;
    color: #2c3e50;
    position: relative;
    padding-left: 12px;
}

.search-input {
    display: flex;
    gap: 10px;
    flex: 1;
}

.add-button {
    padding: 12px 25px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.3s ease;
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
    position: relative;
    overflow: hidden;
    margin-left: 10px;
}

.search-button::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    transform: rotate(45deg);
    animation: shine 3s infinite;
}

.search-button {
    padding: 12px 25px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.3s ease;
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
    position: relative;
    overflow: hidden;
    /* height: 100%; */
}

.add-button:hover,
.search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
    /* height: 100%; */
}

.add-button::after,
.search-button::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    transform: rotate(45deg);
    animation: shine 3s infinite;
}

@keyframes shine {
    0% {
        transform: translateX(-100%) rotate(45deg);
    }

    100% {
        transform: translateX(100%) rotate(45deg);
    }
}

:deep(.el-input-group__append) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    padding: 0;
}

:deep(.el-input-group__append .el-button) {
    border: none;
    margin: 0;
    background: transparent;
    color: white;
    padding: 0 20px;
    height: 100%;
}

:deep(.el-input-group__append .el-button:hover) {
    background: rgba(255, 255, 255, 0.1);
}

:deep(.el-input__wrapper) {
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08);
    width: 100%;
}

:deep(.el-input__inner) {
    height: 40px;
    transition: all 0.3s ease;
}

:deep(.el-input__inner:focus) {
    border-color: #4a9e5c;
    box-shadow: 0 0 8px rgba(74, 158, 92, 0.2);
}

:deep(.el-table) {
    background: transparent;
    border-radius: 12px;
    overflow: hidden;
}

:deep(.el-table__header) {
    background: linear-gradient(90deg, rgba(74, 158, 92, 0.1), rgba(33, 150, 243, 0.1));
}

:deep(.el-table__header-wrapper th) {
    background: transparent;
    color: #2c3e50;
    font-weight: 600;
    border-bottom: 2px solid rgba(74, 158, 92, 0.2);
}

:deep(.el-table__row) {
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
    background: rgba(255, 255, 255, 0.95);
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

:deep(.el-table__cell) {
    border-bottom: 1px solid rgba(74, 158, 92, 0.1);
}

/* 鎸夐挳鏍峰紡 */
:deep(.el-button) {
    border-radius: 6px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

:deep(.el-button--primary) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
}

:deep(.el-button--primary:hover) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(74, 158, 92, 0.2);
}

/* 鍒嗛〉鏍峰紡 */
.pagination {
    margin-top: 20px;
    padding: 15px;
    display: flex;
    justify-content: flex-end;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08);
}

/* 瀵硅瘽妗嗘牱寮?*/
:deep(.el-dialog) {
    border-radius: 15px;
    overflow: hidden;
}

:deep(.el-dialog__header) {
    background: linear-gradient(135deg, #f8faff 0%, #f0f2f5 100%);
    padding: 20px;
    margin: 0;
    position: relative;
}

:deep(.el-dialog__header::before) {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
}

/* 鎻忚堪鍒楄〃鏍峰紡 */
:deep(.el-descriptions) {
    padding: 20px;
}

:deep(.el-descriptions__cell) {
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
}

/* 娣诲姞绉戞妧鎰熻楗板厓绱?*/
/* .medical-records::after {
    content: '';
    position: absolute;
    right: -50px;
    bottom: -50px;
    width: 200px;
    height: 200px;
    background: linear-gradient(45deg, rgba(74, 158, 92, 0.05), rgba(33, 150, 243, 0.05));
    clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
    z-index: 0;
} */

/* 鏍囩鏍峰紡 */
:deep(.el-tag) {
    border-radius: 4px;
}

:deep(.el-tag--success) {
    background: linear-gradient(135deg, #4a9e5c 0%, #81C784 100%);
    border: none;
    color: white;
}

:deep(.el-tag--info) {
    background: linear-gradient(135deg, #90A4AE 0%, #B0BEC5 100%);
    border: none;
    color: white;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* 娣诲姞鍖哄潡閾惧湴鍧€鏍煎紡鎻愮ず鏍峰紡 */
.form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    line-height: 1.2;
}

/* 鍘婚櫎鍐呭祵琛ㄥ崟椤圭殑杈硅窛 */
.no-margin {
    margin: 0 !important;
}

/* 琛ㄥ崟楠岃瘉鐘舵€佹牱寮?*/
:deep(.el-form-item.is-error .el-input__wrapper) {
    box-shadow: 0 0 0 1px #f56c6c !important;
}

:deep(.el-form-item.is-success .el-input__wrapper) {
    box-shadow: 0 0 0 1px #67c23a !important;
}
</style>
